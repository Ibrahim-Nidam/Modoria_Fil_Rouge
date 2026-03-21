package com.modoria.order.application.service;

import com.modoria.order.application.dto.OrderResponseDTO;
import com.modoria.order.application.mapper.OrderMapper;
import com.modoria.order.domain.model.Order;
import com.modoria.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl service;

    @Test
    void getAllOrdersForAdmin_returnsMappedOrders() {
        Order order = Order.builder().id(9L).build();
        OrderResponseDTO dto = OrderResponseDTO.builder().id(9L).build();

        when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(order));
        when(orderMapper.toOrderResponseDTO(any(Order.class))).thenReturn(dto);

        List<OrderResponseDTO> result = service.getAllOrdersForAdmin();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(9L);
    }
}
