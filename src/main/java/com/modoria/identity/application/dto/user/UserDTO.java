package com.modoria.identity.application.dto.user;

import com.modoria.identity.application.dto.role.RoleDTO;

import java.util.Set;

public record UserDTO(
        Long id,
        String fullName,
        String email,
        Boolean enabled,
        Set<RoleDTO> roles) {
}
