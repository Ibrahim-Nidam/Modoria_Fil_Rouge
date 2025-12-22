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
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public AuthResponseDTO register(RegisterRequestDTO requestDTO){
        if(userRepository.findByEmail(requestDTO.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email already Exists");
        }

        Set<Role> roles;
        if (requestDTO.getRole() != null && !requestDTO.getRole().isBlank()) {
            Role specifiedRole = roleRepository.findByName(requestDTO.getRole().toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Role '" + requestDTO.getRole() + "' not found"
                    ));
            roles = Set.of(specifiedRole);
        } else {
            Role customerRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new ResourceNotFoundException("Default role 'CUSTOMER' not found"));
            roles = Set.of(customerRole);
        }

        User user = User.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .enabled(true)
                .roles(roles)
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
        try{

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(), requestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String access = jwtTokenProvider.generateToken(requestDTO.getEmail());
        String refresh = refreshTokenProvider.generateRefreshToken(requestDTO.getEmail());

        return AuthResponseDTO.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .build();

        }catch (Exception ex){
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @Override
    public String logout(String token){
        return "logged out";
    }

}
