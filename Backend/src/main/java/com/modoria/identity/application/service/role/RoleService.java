package com.modoria.identity.application.service.role;

import com.modoria.identity.application.dto.role.RoleDTO;

import java.util.List;

public interface RoleService {
    RoleDTO getRoleName(String name);

    List<RoleDTO> getAllRoles();
}
