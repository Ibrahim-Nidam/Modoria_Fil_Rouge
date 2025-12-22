package com.modoria.dto.user;

import com.modoria.dto.role.RoleDTO;

import java.util.Set;

public record UserDTO(
        Long id,
        String fullName,
        String email,
        Boolean enabled,
        Set<RoleDTO> roles
) {}