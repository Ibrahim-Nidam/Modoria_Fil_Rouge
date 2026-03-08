package com.modoria.chat.infrastructure.web;

import com.modoria.chat.application.dto.SupportSessionResponseDTO;
import com.modoria.chat.application.service.SupportSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support/sessions")
@RequiredArgsConstructor
public class SupportSessionController {

    private final SupportSessionService supportSessionService;

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    public ResponseEntity<List<SupportSessionResponseDTO>> getActiveSessions() {
        return ResponseEntity.ok(supportSessionService.getActiveAgentSessions());
    }

    @PostMapping("/{sessionId}/assign/{agentId}")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    public ResponseEntity<SupportSessionResponseDTO> assignAgent(
            @PathVariable Long sessionId,
            @PathVariable Long agentId) {
        return ResponseEntity.ok(supportSessionService.assignAgent(sessionId, agentId));
    }

    @PostMapping("/customer/{customerId}/close")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    public ResponseEntity<Void> closeSession(@PathVariable Long customerId) {
        supportSessionService.closeSession(customerId);
        return ResponseEntity.noContent().build();
    }
}
