package com.modoria.identity.application.service.user;

import com.modoria.identity.application.dto.user.UserDTO;
import com.modoria.identity.application.dto.user.UserProfileResponseDTO;
import com.modoria.identity.application.dto.user.UserProfileUpdateRequestDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO dto);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserProfileResponseDTO getCurrentUserProfile(String email);

    UserProfileResponseDTO updateUserProfile(String email, UserProfileUpdateRequestDTO updateRequest);
}
