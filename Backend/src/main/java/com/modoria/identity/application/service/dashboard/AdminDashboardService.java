package com.modoria.identity.application.service.dashboard;

import com.modoria.chat.domain.enums.SupportSessionStatus;
import com.modoria.chat.domain.repository.SupportSessionRepository;
import com.modoria.identity.application.dto.dashboard.AdminDashboardStatsResponseDTO;
import com.modoria.order.domain.model.OrderStatus;
import com.modoria.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final SupportSessionRepository supportSessionRepository;

    @Transactional(readOnly = true)
    public AdminDashboardStatsResponseDTO getDashboardStats() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        long totalOrders = orderRepository.count();
        long completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED);
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        long ordersThisMonth = orderRepository.countByCreatedAtGreaterThanEqual(monthStart);

        long totalTickets = supportSessionRepository.count();
        long openTickets = supportSessionRepository.countByStatus(SupportSessionStatus.OPEN);
        long inProgressTickets = supportSessionRepository.countByStatus(SupportSessionStatus.IN_PROGRESS);
        long resolvedTickets = supportSessionRepository.countByStatus(SupportSessionStatus.RESOLVED);
        long ticketsThisMonth = supportSessionRepository.countByCreatedAtGreaterThanEqual(monthStart);
        long unassignedTickets = supportSessionRepository.countByAgentIsNullAndStatusIn(
                List.of(SupportSessionStatus.OPEN, SupportSessionStatus.IN_PROGRESS));

        return AdminDashboardStatsResponseDTO.builder()
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .pendingOrders(pendingOrders)
                .cancelledOrders(cancelledOrders)
                .ordersThisMonth(ordersThisMonth)
                .completedSalesTotal(orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED))
                .completedSalesThisMonth(
                        orderRepository.sumTotalAmountByStatusAndCreatedAtGreaterThanEqual(OrderStatus.COMPLETED,
                                monthStart))
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .unassignedTickets(unassignedTickets)
                .ticketsThisMonth(ticketsThisMonth)
                .build();
    }
}
