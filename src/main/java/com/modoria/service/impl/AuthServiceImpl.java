package com.modoria.service.impl;

import com.modoria.dto.auth.AuthResponseDTO;
import com.modoria.dto.auth.LoginRequestDTO;
import com.modoria.dto.auth.RegisterRequestDTO;
import com.modoria.entity.Role;
import com.modoria.entity.User;
import com.modoria.exception.DuplicateResourceException;
import com.modoria.exception.InvalidCredentialsException;
import com.modoria.exception.ResourceNotFoundException;
import com.modoria.repository.RoleRepository;
import com.modoria.repository.UserRepository;
import com.modoria.security.JwtTokenProvider;
import com.modoria.security.RefreshTokenProvider;
import com.modoria.service.interfaces.AuthService;
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
                "Bearer"
        );
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.email(),
                            requestDTO.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new AuthResponseDTO(
                    jwtTokenProvider.generateToken(requestDTO.email()),
                    refreshTokenProvider.generateRefreshToken(requestDTO.email()),
                    "Bearer"
            );
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
                            "Role '" + requestedRole + "' not found"
                    ));
            return Set.of(role);
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role 'CUSTOMER' not found"));

        return Set.of(customerRole);
    }
}