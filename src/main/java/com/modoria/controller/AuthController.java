package com.modoria.controller;

import com.modoria.dto.auth.AuthResponseDTO;
import com.modoria.dto.auth.LoginRequestDTO;
import com.modoria.dto.auth.RegisterRequestDTO;
import com.modoria.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody RegisterRequestDTO requestDTO){
        return authService.register(requestDTO);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO requestDTO){
        return authService.login(requestDTO);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token){
        return authService.logout(token);
    }
}
