package com.modoria.identity.infrastructure.web;

import com.modoria.identity.application.dto.user.AdminUserCreateRequestDTO;
import com.modoria.identity.application.dto.user.AdminUserResponseDTO;
import com.modoria.identity.application.dto.user.AdminUserUpdateRequestDTO;
import com.modoria.identity.application.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<AdminUserResponseDTO> createUser(@Valid @RequestBody AdminUserCreateRequestDTO requestDTO) {
        return new ResponseEntity<>(userService.createAdminUser(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponseDTO>> getUsers() {
        return ResponseEntity.ok(userService.getAdminUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getAdminUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateAdminUser(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteAdminUser(id);
        return ResponseEntity.noContent().build();
    }
}