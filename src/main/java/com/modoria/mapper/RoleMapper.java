package com.modoria.mapper;

import com.modoria.dto.role.RoleDTO;
import com.modoria.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    Role toEntity(RoleDTO roleDTO);
}
