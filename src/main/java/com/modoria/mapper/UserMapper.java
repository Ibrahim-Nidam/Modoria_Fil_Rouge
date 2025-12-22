package com.modoria.mapper;

import com.modoria.dto.user.UserDTO;
import com.modoria.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);
}
