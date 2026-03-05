package com.modoria.order.application.service;

import com.modoria.order.application.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO checkoutCart();

    List<OrderResponseDTO> getUserOrders();

    OrderResponseDTO getOrderById(Long id);
}
