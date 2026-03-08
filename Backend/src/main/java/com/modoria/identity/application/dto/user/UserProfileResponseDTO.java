package com.modoria.identity.application.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private Set<String> roles;
}
