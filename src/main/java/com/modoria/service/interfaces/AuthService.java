package com.modoria.service.interfaces;

import com.modoria.dto.auth.AuthResponseDTO;
import com.modoria.dto.auth.LoginRequestDTO;
import com.modoria.dto.auth.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO requestDTO);
    AuthResponseDTO login(LoginRequestDTO requestDTO);
    String logout(String token);
}
