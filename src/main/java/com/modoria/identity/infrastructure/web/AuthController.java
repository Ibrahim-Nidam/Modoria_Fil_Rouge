package com.modoria.identity.infrastructure.web;

import com.modoria.identity.application.dto.auth.AuthResponseDTO;
import com.modoria.identity.application.dto.auth.LoginRequestDTO;
import com.modoria.identity.application.dto.auth.RegisterRequestDTO;
import com.modoria.identity.application.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody RegisterRequestDTO requestDTO) {
        return authService.register(requestDTO);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO requestDTO) {
        return authService.login(requestDTO);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        return authService.logout(token);
    }
}
