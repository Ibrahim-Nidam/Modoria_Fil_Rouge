package com.modoria.identity.application.service.user;

import com.modoria.identity.application.dto.user.AdminUserCreateRequestDTO;
import com.modoria.identity.application.dto.user.AdminUserResponseDTO;
import com.modoria.identity.application.dto.user.AdminUserUpdateRequestDTO;
import com.modoria.identity.application.dto.user.UserDTO;
import com.modoria.identity.application.dto.user.UserProfileResponseDTO;
import com.modoria.identity.application.dto.user.UserProfileUpdateRequestDTO;
import com.modoria.identity.application.mapper.user.UserMapper;
import com.modoria.identity.domain.model.Role;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.RoleRepository;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.shared.exception.BadRequestException;
import com.modoria.shared.exception.DuplicateResourceException;
import com.modoria.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public AdminUserResponseDTO createAdminUser(AdminUserCreateRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("User with email '" + dto.getEmail() + "' already exists");
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .enabled(dto.getEnabled() == null ? true : dto.getEnabled())
                .roles(resolveRoles(dto.getRoles()))
                .build();

        return userMapper.toAdminResponseDTO(userRepository.save(user));
    }

    @Override
    public UserDTO createUser(UserDTO dto) {
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Override
    public void deleteAdminUser(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    @Override
    public List<AdminUserResponseDTO> getAdminUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toAdminResponseDTO)
                .toList();
    }

    @Override
    public AdminUserResponseDTO getAdminUserById(Long id) {
        return userMapper.toAdminResponseDTO(findUserOrThrow(id));
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = findUserOrThrow(id);
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public UserProfileResponseDTO getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toProfileResponseDTO(user);
    }

    @Override
    public AdminUserResponseDTO updateAdminUser(Long id, AdminUserUpdateRequestDTO dto) {
        User user = findUserOrThrow(id);

        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException("User with email '" + dto.getEmail() + "' already exists");
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.getEnabled() == null ? user.getEnabled() : dto.getEnabled());
        user.setRoles(resolveRoles(dto.getRoles()));

        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userMapper.toAdminResponseDTO(userRepository.save(user));
    }

    @Override
    public UserProfileResponseDTO updateUserProfile(String email, UserProfileUpdateRequestDTO updateRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (userRepository.existsByEmailAndIdNot(updateRequest.getEmail(), user.getId())) {
            throw new DuplicateResourceException("User with email '" + updateRequest.getEmail() + "' already exists");
        }

        userMapper.updateEntityFromDTO(updateRequest, user);

        if (StringUtils.hasText(updateRequest.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        User saved = userRepository.save(user);
        return userMapper.toProfileResponseDTO(saved);
    }

    @Override
    public void deleteCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new BadRequestException("At least one role is required");
        }

        return roleNames.stream()
                .map(this::normalizeRoleName)
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BadRequestException("Role not found: " + roleName)))
                .collect(java.util.stream.Collectors.toSet());
    }

    private String normalizeRoleName(String roleName) {
        String normalizedRoleName = roleName == null ? "" : roleName.trim().toUpperCase();
        return normalizedRoleName.startsWith("ROLE_")
                ? normalizedRoleName.substring(5)
                : normalizedRoleName;
    }
}
