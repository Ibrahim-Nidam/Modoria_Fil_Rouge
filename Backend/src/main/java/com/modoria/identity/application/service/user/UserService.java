package com.modoria.identity.application.service.user;

import com.modoria.identity.application.dto.user.AdminUserCreateRequestDTO;
import com.modoria.identity.application.dto.user.AdminUserResponseDTO;
import com.modoria.identity.application.dto.user.AdminUserUpdateRequestDTO;
import com.modoria.identity.application.dto.user.UserDTO;
import com.modoria.identity.application.dto.user.UserProfileResponseDTO;
import com.modoria.identity.application.dto.user.UserProfileUpdateRequestDTO;

import java.util.List;

public interface UserService {
    AdminUserResponseDTO createAdminUser(AdminUserCreateRequestDTO dto);

    UserDTO createUser(UserDTO dto);

    void deleteAdminUser(Long id);

    List<AdminUserResponseDTO> getAdminUsers();

    AdminUserResponseDTO getAdminUserById(Long id);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserProfileResponseDTO getCurrentUserProfile(String email);

    AdminUserResponseDTO updateAdminUser(Long id, AdminUserUpdateRequestDTO dto);

    UserProfileResponseDTO updateUserProfile(String email, UserProfileUpdateRequestDTO updateRequest);

    void deleteCurrentUserProfile(String email);
}
