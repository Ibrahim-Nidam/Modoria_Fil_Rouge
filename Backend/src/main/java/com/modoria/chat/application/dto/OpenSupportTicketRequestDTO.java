package com.modoria.chat.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenSupportTicketRequestDTO {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotBlank(message = "Ticket subject is required")
    @Size(max = 150, message = "Ticket subject is too long")
    private String subject;

    @NotBlank(message = "Ticket message is required")
    @Size(max = 4000, message = "Ticket message is too long")
    private String message;
}
