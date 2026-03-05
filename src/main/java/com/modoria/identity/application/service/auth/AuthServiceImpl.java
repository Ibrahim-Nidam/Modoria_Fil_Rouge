package com.modoria.identity.application.service.auth;

import com.modoria.identity.application.dto.auth.AuthResponseDTO;
import com.modoria.identity.application.dto.auth.LoginRequestDTO;
import com.modoria.identity.application.dto.auth.RegisterRequestDTO;
import com.modoria.identity.domain.model.Role;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.RoleRepository;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.identity.infrastructure.security.JwtTokenProvider;
import com.modoria.identity.infrastructure.security.RefreshTokenProvider;
import com.modoria.shared.exception.DuplicateResourceException;
import com.modoria.shared.exception.InvalidCredentialsException;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AuthenticationManager authenticationManager;

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
                "Bearer");
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.email(),
                            requestDTO.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new AuthResponseDTO(
                    jwtTokenProvider.generateToken(requestDTO.email()),
                    refreshTokenProvider.generateRefreshToken(requestDTO.email()),
                    "Bearer");
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @Override
    public String logout(String token) {
        return "logged out";
    }

    private Set<Role> determineRoles(String requestedRole) {
        if (requestedRole != null && !requestedRole.isBlank()) {
            Role role = roleRepository.findByName(requestedRole.toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Role '" + requestedRole + "' not found"));
            return Set.of(role);
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role 'CUSTOMER' not found"));

        return Set.of(customerRole);
    }
}
