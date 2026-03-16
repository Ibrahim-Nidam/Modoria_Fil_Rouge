package com.modoria.chat.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportSessionReplyRequestDTO {

    @NotBlank(message = "Reply message is required")
    @Size(max = 4000, message = "Reply message is too long")
    private String message;
}
