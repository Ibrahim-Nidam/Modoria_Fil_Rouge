package com.modoria.order.application.service;

import com.modoria.identity.domain.model.User;
import com.modoria.identity.infrastructure.security.CustomUserDetails;
import com.modoria.order.application.dto.OrderResponseDTO;
import com.modoria.order.application.mapper.OrderMapper;
import com.modoria.order.domain.model.OrderStatus;
import com.modoria.order.domain.repository.OrderRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders() {
        User currentUser = getCurrentUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrdersForAdmin() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .filter(o -> o.getUser().getId().equals(getCurrentUser().getId()))
                .map(orderMapper::toOrderResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or access denied"));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByIdForAdmin(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toOrderResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatusForAdmin(Long id, OrderStatus status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return orderMapper.toOrderResponseDTO(orderRepository.save(order));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUser();
    }
}
