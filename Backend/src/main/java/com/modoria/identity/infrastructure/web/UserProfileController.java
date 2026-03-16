package com.modoria.identity.infrastructure.web;

import com.modoria.identity.application.dto.user.UserProfileResponseDTO;
import com.modoria.identity.application.dto.user.UserProfileUpdateRequestDTO;
import com.modoria.identity.application.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileResponseDTO> getCurrentUserProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getCurrentUserProfile(userDetails.getUsername()));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateRequestDTO updateRequest) {
        return ResponseEntity.ok(userService.updateUserProfile(userDetails.getUsername(), updateRequest));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentUserProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
