package com.modoria.identity.application.service.user;

import com.modoria.identity.application.dto.user.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO dto);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();
}
