package com.modoria.identity.application.service.auth;

import com.modoria.identity.application.dto.auth.AuthResponseDTO;
import com.modoria.identity.application.dto.auth.LoginRequestDTO;
import com.modoria.identity.application.dto.auth.RegisterRequestDTO;
import com.modoria.identity.application.dto.auth.RefreshTokenRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO requestDTO);

    AuthResponseDTO login(LoginRequestDTO requestDTO);

    String logout(String token);

    AuthResponseDTO refreshToken(RefreshTokenRequestDTO requestDTO);

    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);
}
