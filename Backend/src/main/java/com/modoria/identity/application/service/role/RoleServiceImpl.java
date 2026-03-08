package com.modoria.identity.application.service.role;

import com.modoria.identity.application.dto.role.RoleDTO;
import com.modoria.identity.application.mapper.role.RoleMapper;
import com.modoria.identity.domain.model.Role;
import com.modoria.identity.domain.repository.RoleRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDTO getRoleName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name));
        return roleMapper.toDTO(role);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .toList();
    }
}
