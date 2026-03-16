package com.modoria.identity.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequestDTO {
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name is too long")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email is too long")
    private String email;

    @Size(max = 20, message = "Phone number is too long")
    private String phoneNumber;

    @Size(max = 255, message = "Address is too long")
    private String address;

    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;
}
