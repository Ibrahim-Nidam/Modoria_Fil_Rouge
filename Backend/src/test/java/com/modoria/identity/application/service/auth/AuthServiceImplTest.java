package com.modoria.identity.application.service.auth;

import com.modoria.identity.domain.repository.PasswordResetTokenRepository;
import com.modoria.identity.domain.repository.RoleRepository;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.identity.infrastructure.security.JwtTokenProvider;
import com.modoria.identity.infrastructure.security.RefreshTokenProvider;
import com.modoria.identity.infrastructure.security.TokenBlacklistService;
import com.modoria.identity.application.dto.auth.LoginRequestDTO;
import com.modoria.shared.email.EmailService;
import com.modoria.shared.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthServiceImpl service;

    @Test
    void login_whenAuthenticationFails_throwsInvalidCredentials() {
        LoginRequestDTO request = new LoginRequestDTO("user@modoria.com", "wrong-password");
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
