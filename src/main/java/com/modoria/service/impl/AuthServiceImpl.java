package com.modoria.service.impl;

import com.modoria.dto.auth.AuthResponseDTO;
import com.modoria.dto.auth.LoginRequestDTO;
import com.modoria.dto.auth.RegisterRequestDTO;
import com.modoria.model.Role;
import com.modoria.model.User;
import com.modoria.repository.RoleRepository;
import com.modoria.repository.UserRepository;
import com.modoria.security.JwtTokenProvider;
import com.modoria.security.RefreshTokenProvider;
import com.modoria.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public AuthResponseDTO register(RegisterRequestDTO requestDTO){
        if(userRepository.findByEmail(requestDTO.getEmail()).isPresent()){
            throw new RuntimeException("Email already Exists");
        }

        Role defaultRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Default role not Found"));

        User user = User.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .password(requestDTO.getPassword())
                .enabled(true)
                .roles(Set.of(defaultRole))
                .build();

        userRepository.save(user);

        String access = jwtTokenProvider.generateToken(user.getEmail());
        String refresh = refreshTokenProvider.generateRefreshToken(user.getEmail());

        return AuthResponseDTO.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO requestDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(), requestDTO.getPassword()
                )
        );
        String access = jwtTokenProvider.generateToken(requestDTO.getEmail());
        String refresh = refreshTokenProvider.generateRefreshToken(requestDTO.getEmail());

        return AuthResponseDTO.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .build();

    }

    @Override
    public String logout(String token){
        return "logged out";
    }

}
