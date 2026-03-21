package com.modoria.identity.application.dto.user;

import java.util.Set;

public record AdminUserResponseDTO(
        Long id,
        String fullName,
        String email,
        Boolean enabled,
        Boolean deleted,
        Set<String> roles) {
}