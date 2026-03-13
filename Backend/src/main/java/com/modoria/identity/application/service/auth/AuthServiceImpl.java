package com.modoria.identity.application.service.auth;

import com.modoria.identity.application.dto.auth.AuthResponseDTO;
import com.modoria.identity.application.dto.auth.LoginRequestDTO;
import com.modoria.identity.application.dto.auth.RegisterRequestDTO;
import com.modoria.identity.application.dto.auth.RefreshTokenRequestDTO;
import com.modoria.identity.application.dto.role.RoleDTO;
import com.modoria.identity.application.dto.user.UserDTO;
import com.modoria.identity.domain.model.PasswordResetToken;
import com.modoria.identity.domain.model.Role;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.PasswordResetTokenRepository;
import com.modoria.identity.domain.repository.RoleRepository;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.identity.infrastructure.security.JwtTokenProvider;
import com.modoria.identity.infrastructure.security.RefreshTokenProvider;
import com.modoria.identity.infrastructure.security.TokenBlacklistService;
import com.modoria.shared.email.EmailService;
import com.modoria.shared.exception.DuplicateResourceException;
import com.modoria.shared.exception.InvalidCredentialsException;
import com.modoria.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {
        userRepository.findByEmail(requestDTO.email())
                .ifPresent(u -> {
                    throw new DuplicateResourceException("Email already exists");
                });

        Set<Role> roles = determineRoles(requestDTO.role());

        User user = User.builder()
                .fullName(requestDTO.fullName())
                .email(requestDTO.email())
                .password(passwordEncoder.encode(requestDTO.password()))
                .enabled(true)
                .roles(roles)
                .build();

        userRepository.save(user);

        return new AuthResponseDTO(
                jwtTokenProvider.generateToken(user.getEmail()),
                refreshTokenProvider.generateRefreshToken(user.getEmail()),
                "Bearer",
                mapToUserDTO(user));
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.email(),
                            requestDTO.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(requestDTO.email())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            return new AuthResponseDTO(
                    jwtTokenProvider.generateToken(requestDTO.email()),
                    refreshTokenProvider.generateRefreshToken(requestDTO.email()),
                    "Bearer",
                    mapToUserDTO(user));
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @Override
    public String logout(String token) {
        if (token != null) {
            tokenBlacklistService.blacklistToken(token);
        }
        return "Logged out successfully";
    }

    @Override
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO requestDTO) {
        String token = requestDTO.refreshToken();
        if (token == null || !refreshTokenProvider.validate(token)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String email = refreshTokenProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthResponseDTO(
                jwtTokenProvider.generateToken(email),
                refreshTokenProvider.generateRefreshToken(email),
                "Bearer",
                mapToUserDTO(user));
    }

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", email);
            return;
        }

        User user = userOptional.get();

        // Remove any existing token for this user (one active reset at a time)
        passwordResetTokenRepository.deleteByUser(user);

        String rawToken = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(rawToken, user, 30);
        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(email, rawToken);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired password reset token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new ResourceNotFoundException("Password reset token has expired. Please request a new one.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
        log.info("Password successfully reset for user: {}", user.getEmail());
    }

    private Set<Role> determineRoles(String requestedRole) {
        if (requestedRole != null && !requestedRole.isBlank()) {
            String normalizedRole = normalizeRoleName(requestedRole);
            Role role = roleRepository.findByName(normalizedRole)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Role '" + requestedRole + "' not found"));
            return Set.of(role);
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role 'CUSTOMER' not found"));

        return Set.of(customerRole);
    }

    private String normalizeRoleName(String roleName) {
        String normalizedRoleName = roleName == null ? "" : roleName.trim().toUpperCase();
        return normalizedRoleName.startsWith("ROLE_")
                ? normalizedRoleName.substring(5)
                : normalizedRoleName;
    }

    private UserDTO mapToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getEnabled(),
                user.getRoles().stream()
                        .map(role -> new RoleDTO(role.getId(), role.getName()))
                        .collect(java.util.stream.Collectors.toSet()));
    }
}
