package com.modoria.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthResponseDTO(
        @NotBlank String accessToken,
        @NotBlank String refreshToken,
        @NotBlank String tokenType
) {}