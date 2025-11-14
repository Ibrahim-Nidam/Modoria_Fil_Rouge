package com.modoria.mapper;

import com.modoria.dto.UserDTO;
import com.modoria.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);
}
