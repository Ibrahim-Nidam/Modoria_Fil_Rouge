package com.modoria.service.interfaces;

import com.modoria.dto.role.RoleDTO;

import java.util.List;

public interface RoleService {
    RoleDTO getRoleName(String name);
    List<RoleDTO> getAllRoles();
}
