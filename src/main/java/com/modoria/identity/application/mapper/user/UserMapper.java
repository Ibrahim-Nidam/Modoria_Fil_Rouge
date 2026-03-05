package com.modoria.identity.application.mapper.user;

import com.modoria.identity.application.dto.user.UserDTO;
import com.modoria.identity.application.mapper.role.RoleMapper;
import com.modoria.identity.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { RoleMapper.class })
public interface UserMapper {
    UserDTO toDTO(User user);

    User toEntity(UserDTO dto);
}
