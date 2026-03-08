package com.modoria.chat.application.dto;

import com.modoria.chat.domain.enums.SupportSessionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SupportSessionResponseDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long agentId;
    private String agentName;
    private SupportSessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
