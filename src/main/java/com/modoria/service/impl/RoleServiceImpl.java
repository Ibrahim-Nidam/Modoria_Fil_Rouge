package com.modoria.service.impl;

import com.modoria.dto.RoleDTO;
import com.modoria.mapper.RoleMapper;
import com.modoria.model.Role;
import com.modoria.repository.RoleRepository;
import com.modoria.service.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDTO getRoleName(String name){
        Role role = roleRepository.findByName(name)
                .orElseThrow(()-> new RuntimeException("Role not Found : " + name));
        return roleMapper.toDTO(role);
    }

    @Override
    public List<RoleDTO> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .toList();
    }
}
