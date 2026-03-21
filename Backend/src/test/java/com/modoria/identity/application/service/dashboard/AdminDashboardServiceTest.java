package com.modoria.identity.application.service.dashboard;

import com.modoria.chat.domain.enums.SupportSessionStatus;
import com.modoria.chat.domain.repository.SupportSessionRepository;
import com.modoria.identity.application.dto.dashboard.AdminDashboardStatsResponseDTO;
import com.modoria.order.domain.model.OrderStatus;
import com.modoria.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SupportSessionRepository supportSessionRepository;

    @InjectMocks
    private AdminDashboardService service;

    @Test
    void getDashboardStats_returnsAggregatedCounts() {
        when(orderRepository.count()).thenReturn(10L);
        when(orderRepository.countByStatus(OrderStatus.COMPLETED)).thenReturn(4L);
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(5L);
        when(orderRepository.countByStatus(OrderStatus.CANCELLED)).thenReturn(1L);
        when(orderRepository.countByCreatedAtGreaterThanEqual(any(LocalDateTime.class))).thenReturn(3L);
        when(orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED)).thenReturn(BigDecimal.TEN);
        when(orderRepository.sumTotalAmountByStatusAndCreatedAtGreaterThanEqual(any(OrderStatus.class), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ONE);

        when(supportSessionRepository.count()).thenReturn(8L);
        when(supportSessionRepository.countByStatus(SupportSessionStatus.OPEN)).thenReturn(2L);
        when(supportSessionRepository.countByStatus(SupportSessionStatus.IN_PROGRESS)).thenReturn(3L);
        when(supportSessionRepository.countByStatus(SupportSessionStatus.RESOLVED)).thenReturn(3L);
        when(supportSessionRepository.countByCreatedAtGreaterThanEqual(any(LocalDateTime.class))).thenReturn(5L);
        when(supportSessionRepository.countByAgentIsNullAndStatusIn(List.of(SupportSessionStatus.OPEN, SupportSessionStatus.IN_PROGRESS)))
                .thenReturn(2L);

        AdminDashboardStatsResponseDTO stats = service.getDashboardStats();

        assertThat(stats.getTotalOrders()).isEqualTo(10L);
        assertThat(stats.getTotalTickets()).isEqualTo(8L);
    }
}
