package com.modoria.identity.infrastructure.web;

import com.modoria.identity.application.dto.dashboard.AdminDashboardStatsResponseDTO;
import com.modoria.identity.application.service.dashboard.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardStatsResponseDTO> getStats() {
        return ResponseEntity.ok(adminDashboardService.getDashboardStats());
    }
}
