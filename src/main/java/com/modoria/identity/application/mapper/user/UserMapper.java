package com.modoria.identity.application.mapper.user;

import com.modoria.identity.application.dto.user.UserDTO;
import com.modoria.identity.application.dto.user.UserProfileResponseDTO;
import com.modoria.identity.application.dto.user.UserProfileUpdateRequestDTO;
import com.modoria.identity.application.mapper.role.RoleMapper;
import com.modoria.identity.domain.model.Role;
import com.modoria.identity.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { RoleMapper.class })
public interface UserMapper {
    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserProfileResponseDTO toProfileResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntityFromDTO(UserProfileUpdateRequestDTO dto, @MappingTarget User user);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null)
            return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
