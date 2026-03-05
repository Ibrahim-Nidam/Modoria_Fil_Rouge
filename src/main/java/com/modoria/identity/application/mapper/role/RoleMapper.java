package com.modoria.identity.application.mapper.role;

import com.modoria.identity.application.dto.role.RoleDTO;
import com.modoria.identity.domain.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);

    Role toEntity(RoleDTO roleDTO);
}
