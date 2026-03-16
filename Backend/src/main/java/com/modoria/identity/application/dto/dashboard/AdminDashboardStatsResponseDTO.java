package com.modoria.identity.application.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminDashboardStatsResponseDTO {
    private long totalOrders;
    private long completedOrders;
    private long pendingOrders;
    private long cancelledOrders;
    private long ordersThisMonth;

    private BigDecimal completedSalesTotal;
    private BigDecimal completedSalesThisMonth;

    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long unassignedTickets;
    private long ticketsThisMonth;
}
