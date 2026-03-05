package com.modoria.identity.application.service.auth;

import com.modoria.identity.application.dto.auth.AuthResponseDTO;
import com.modoria.identity.application.dto.auth.LoginRequestDTO;
import com.modoria.identity.application.dto.auth.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO requestDTO);

    AuthResponseDTO login(LoginRequestDTO requestDTO);

    String logout(String token);

    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);
}
