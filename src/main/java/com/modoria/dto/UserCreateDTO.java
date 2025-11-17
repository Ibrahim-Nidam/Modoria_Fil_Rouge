package com.modoria.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDTO {
    private String fullName;
    private String email;
    private String password;
}