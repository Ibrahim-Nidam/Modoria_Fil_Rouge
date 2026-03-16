package com.modoria.chat.application.dto;

import com.modoria.chat.domain.enums.SupportSessionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SupportSessionResponseDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long agentId;
    private String agentName;
    private Long orderId;
    private BigDecimal orderTotal;
    private String orderStatus;
    private LocalDateTime orderCreatedAt;
    private String subject;
    private String initialMessage;
    private SupportSessionStatus status;
    private Long resolvedById;
    private String resolvedByName;
    private LocalDateTime closedAt;
    private Long resolutionMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
