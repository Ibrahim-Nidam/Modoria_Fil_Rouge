package com.modoria.order.application.service;

import com.modoria.order.application.dto.OrderResponseDTO;
import com.modoria.order.domain.model.OrderStatus;

import java.util.List;

public interface OrderService {
    List<OrderResponseDTO> getUserOrders();

    List<OrderResponseDTO> getAllOrdersForAdmin();

    OrderResponseDTO getOrderById(Long id);

    OrderResponseDTO getOrderByIdForAdmin(Long id);

    OrderResponseDTO updateOrderStatusForAdmin(Long id, OrderStatus status);
}
