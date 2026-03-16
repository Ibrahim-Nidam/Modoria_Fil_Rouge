package com.modoria.chat.infrastructure.web;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.dto.OpenSupportTicketRequestDTO;
import com.modoria.chat.application.dto.SupportSessionReplyRequestDTO;
import com.modoria.chat.application.dto.SupportSessionResponseDTO;
import com.modoria.chat.application.dto.SupportSessionStatusUpdateRequestDTO;
import com.modoria.chat.application.service.SupportSessionService;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("/api/v1/support/sessions")
@RequiredArgsConstructor
public class SupportSessionController {

    private final SupportSessionService supportSessionService;
    private final UserRepository userRepository;

    @PostMapping("/tickets/open")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupportSessionResponseDTO> openTicket(
            @Valid @RequestBody OpenSupportTicketRequestDTO request,
            Principal principal) {
        User currentUser = getCurrentUser(principal);
        return ResponseEntity.ok(supportSessionService.openTicket(currentUser.getId(), request));
    }

    @GetMapping("/tickets/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupportSessionResponseDTO> getMyLatestTicket(Principal principal) {
        User currentUser = getCurrentUser(principal);
        return supportSessionService.getTicketForCustomer(currentUser.getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/tickets/mine/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SupportSessionResponseDTO>> getMyTickets(Principal principal) {
        User currentUser = getCurrentUser(principal);
        return ResponseEntity.ok(supportSessionService.getTicketsForCustomer(currentUser.getId()));
    }

    @GetMapping("/tickets/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupportSessionResponseDTO> getTicketById(
            @PathVariable Long sessionId,
            Principal principal) {
        User currentUser = getCurrentUser(principal);
        return ResponseEntity.ok(supportSessionService.getTicketByIdForUser(sessionId, currentUser.getId()));
    }

    @GetMapping("/tickets/{sessionId}/conversation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageDTO>> getTicketConversation(
            @PathVariable Long sessionId,
            Principal principal) {
        User currentUser = getCurrentUser(principal);
        return ResponseEntity.ok(supportSessionService.getTicketConversation(sessionId, currentUser.getId()));
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportSessionResponseDTO>> getAllTickets() {
        return ResponseEntity.ok(supportSessionService.getAllTickets());
    }

    @GetMapping("/tickets/assigned")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<List<SupportSessionResponseDTO>> getAssignedTickets(Principal principal) {
        User currentUser = getCurrentUser(principal);
        return ResponseEntity.ok(supportSessionService.getAssignedTickets(currentUser.getId()));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportSessionResponseDTO>> getActiveSessions() {
        return ResponseEntity.ok(supportSessionService.getActiveAgentSessions());
    }

    @PostMapping("/{sessionId}/assign/{agentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupportSessionResponseDTO> assignAgent(
            @PathVariable Long sessionId,
            @PathVariable Long agentId) {
        return ResponseEntity.ok(supportSessionService.assignAgent(sessionId, agentId));
    }

    @PostMapping("/{sessionId}/reply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageDTO> replyToTicket(
            @PathVariable Long sessionId,
            @Valid @RequestBody SupportSessionReplyRequestDTO request,
            Principal principal) {
        User currentUser = getCurrentUser(principal);
        return ResponseEntity.ok(supportSessionService.replyToCustomerTicket(sessionId, currentUser.getId(), request.getMessage()));
    }

    @PatchMapping("/{sessionId}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<SupportSessionResponseDTO> updateTicketStatus(
            @PathVariable Long sessionId,
            @Valid @RequestBody SupportSessionStatusUpdateRequestDTO request,
            Principal principal) {
        User currentUser = getCurrentUser(principal);
        return ResponseEntity.ok(supportSessionService.updateTicketStatus(sessionId, currentUser.getId(), request));
    }

    @PostMapping("/customer/{customerId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> closeSession(@PathVariable Long customerId) {
        supportSessionService.closeSession(customerId);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
