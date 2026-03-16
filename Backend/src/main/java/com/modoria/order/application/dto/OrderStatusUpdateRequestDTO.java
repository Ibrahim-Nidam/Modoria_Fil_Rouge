package com.modoria.order.application.dto;

import com.modoria.order.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequestDTO {

    @NotNull(message = "Order status is required")
    private OrderStatus status;
}