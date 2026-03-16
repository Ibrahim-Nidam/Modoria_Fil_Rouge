package com.modoria.chat.application.service;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.dto.ChatMessageRequest;
import com.modoria.chat.application.dto.OpenSupportTicketRequestDTO;
import com.modoria.chat.application.dto.SupportSessionStatusUpdateRequestDTO;
import com.modoria.chat.domain.enums.SupportSessionStatus;
import com.modoria.chat.domain.model.SupportSession;
import com.modoria.chat.domain.repository.SupportSessionRepository;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.order.domain.model.Order;
import com.modoria.order.domain.repository.OrderRepository;
import com.modoria.shared.exception.BadRequestException;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import com.modoria.chat.application.dto.SupportSessionResponseDTO;

@Service
@RequiredArgsConstructor
public class SupportSessionService {

    private final SupportSessionRepository supportSessionRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final OrderRepository orderRepository;

    @Transactional
    public SupportSessionResponseDTO openTicket(Long requesterId, OpenSupportTicketRequestDTO request) {
        User requester = findUserOrThrow(requesterId);

        if (hasRole(requester, "AGENT")) {
            throw new BadRequestException("Agents cannot open support tickets");
        }

        if (!hasRole(requester, "ADMIN") && !hasRole(requester, "CLIENT")) {
            throw new BadRequestException("Only admins and clients can open tickets");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (hasRole(requester, "CLIENT") && !order.getUser().getId().equals(requester.getId())) {
            throw new BadRequestException("Ticket order must belong to the current client");
        }

        SupportSession session = SupportSession.builder()
                .customer(order.getUser())
                .order(order)
                .subject(request.getSubject().trim())
                .initialMessage(request.getMessage().trim())
                .status(SupportSessionStatus.OPEN)
                .build();

        SupportSession saved = supportSessionRepository.save(session);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public Optional<SupportSessionResponseDTO> getTicketForCustomer(Long customerId) {
        return supportSessionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .findFirst()
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public List<SupportSessionResponseDTO> getTicketsForCustomer(Long customerId) {
        return supportSessionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportSessionResponseDTO> getAllTickets() {
        return supportSessionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportSessionResponseDTO> getAssignedTickets(Long agentId) {
        return supportSessionRepository.findByAgentIdOrderByCreatedAtDesc(agentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupportSessionResponseDTO getTicketByIdForUser(Long sessionId, Long requesterId) {
        SupportSession session = findSessionOrThrow(sessionId);
        User requester = findUserOrThrow(requesterId);
        ensureTicketAccess(session, requester);
        return mapToDTO(session);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getTicketConversation(Long sessionId, Long requesterId) {
        SupportSession session = findSessionOrThrow(sessionId);
        User requester = findUserOrThrow(requesterId);
        ensureTicketAccess(session, requester);
        return chatService.getTicketConversation(sessionId);
    }

    @Transactional
    public ChatMessageDTO sendTicketMessage(Long sessionId, Long senderId, String message) {
        SupportSession session = findSessionOrThrow(sessionId);
        User sender = findUserOrThrow(senderId);

        if (session.getStatus() == SupportSessionStatus.RESOLVED || session.getStatus() == SupportSessionStatus.CLOSED) {
            throw new BadRequestException("Resolved tickets are closed for replies");
        }

        String trimmedMessage = message == null ? "" : message.trim();
        if (trimmedMessage.isEmpty()) {
            throw new BadRequestException("Reply message is required");
        }

        User receiver = resolveTicketReceiver(session, sender);

        if (session.getStatus() == SupportSessionStatus.OPEN) {
            session.setStatus(SupportSessionStatus.IN_PROGRESS);
        }

        if (!session.getCustomer().getId().equals(sender.getId()) && session.getAgent() == null) {
            session.setAgent(sender);
        }

        if (session.getStatus() != SupportSessionStatus.RESOLVED) {
            session.setClosedAt(null);
            session.setResolvedBy(null);
        }

        supportSessionRepository.save(session);

        ChatMessageRequest chatRequest = ChatMessageRequest.builder()
                .receiverId(receiver.getId())
                .content(trimmedMessage)
                .build();

        return chatService.sendMessage(sender.getId(), chatRequest, session.getId());
    }

    @Transactional
    public ChatMessageDTO replyToCustomerTicket(Long sessionId, Long senderId, String message) {
        return sendTicketMessage(sessionId, senderId, message);
    }

    @Transactional
    public SupportSessionResponseDTO updateTicketStatus(Long sessionId, Long actorId,
            SupportSessionStatusUpdateRequestDTO request) {
        SupportSession session = findSessionOrThrow(sessionId);
        User actor = findUserOrThrow(actorId);

        if (!hasRole(actor, "ADMIN") && !hasRole(actor, "AGENT")) {
            throw new BadRequestException("Only admins and agents can update ticket status");
        }

        if (hasRole(actor, "AGENT")) {
            if (session.getAgent() == null || !session.getAgent().getId().equals(actor.getId())) {
                throw new BadRequestException("Agents can only update their assigned tickets");
            }
        }

        SupportSessionStatus nextStatus = request.getStatus();

        if ((session.getStatus() == SupportSessionStatus.RESOLVED || session.getStatus() == SupportSessionStatus.CLOSED)
                && nextStatus != session.getStatus()) {
            throw new BadRequestException("Resolved tickets cannot be reopened");
        }

        session.setStatus(nextStatus);

        if (nextStatus == SupportSessionStatus.RESOLVED || nextStatus == SupportSessionStatus.CLOSED) {
            session.setClosedAt(LocalDateTime.now());
            session.setResolvedBy(actor);
        } else {
            session.setClosedAt(null);
            session.setResolvedBy(null);
        }

        return mapToDTO(supportSessionRepository.save(session));
    }

    @Transactional
    public SupportSession getOrCreateSession(Long customerId) {
        return supportSessionRepository.findFirstByCustomerIdAndStatusNotOrderByCreatedAtDesc(customerId,
                SupportSessionStatus.RESOLVED)
                .orElseGet(() -> {
                    User customer = userRepository.findById(customerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
                    SupportSession session = SupportSession.builder()
                            .customer(customer)
                            .status(SupportSessionStatus.OPEN)
                            .build();
                    return supportSessionRepository.save(session);
                });
    }

    @Transactional
    public void closeSession(Long customerId) {
        supportSessionRepository.findFirstByCustomerIdAndStatusNotOrderByCreatedAtDesc(customerId,
                SupportSessionStatus.RESOLVED)
                .ifPresent(session -> {
                    session.setStatus(SupportSessionStatus.RESOLVED);
                    session.setClosedAt(LocalDateTime.now());
                    supportSessionRepository.save(session);
                });
    }

    @Transactional(readOnly = true)
    public List<SupportSessionResponseDTO> getActiveAgentSessions() {
        return supportSessionRepository.findByStatus(SupportSessionStatus.IN_PROGRESS)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportSessionResponseDTO assignAgent(Long sessionId, Long agentId) {
        SupportSession session = findSessionOrThrow(sessionId);
        User agent = findUserOrThrow(agentId);

        if (!hasRole(agent, "AGENT") && !hasRole(agent, "ADMIN")) {
            throw new BadRequestException("Target user must be an agent or admin");
        }

        session.setAgent(agent);
        if (session.getStatus() == SupportSessionStatus.OPEN) {
            session.setStatus(SupportSessionStatus.IN_PROGRESS);
        }
        return mapToDTO(supportSessionRepository.save(session));
    }

    private User resolveTicketReceiver(SupportSession session, User sender) {
        if (session.getCustomer().getId().equals(sender.getId())) {
            if (session.getAgent() == null) {
                throw new BadRequestException("No agent has been assigned to this ticket yet");
            }
            return session.getAgent();
        }

        if (hasRole(sender, "ADMIN") || hasRole(sender, "AGENT")) {
            if (hasRole(sender, "AGENT")) {
                if (session.getAgent() == null || !session.getAgent().getId().equals(sender.getId())) {
                    throw new BadRequestException("Agent can only reply to assigned tickets");
                }
            }
            return session.getCustomer();
        }

        throw new BadRequestException("User is not authorized to reply on this ticket");
    }

    private void ensureTicketAccess(SupportSession session, User requester) {
        if (hasRole(requester, "ADMIN")) {
            return;
        }

        if (session.getCustomer().getId().equals(requester.getId())) {
            return;
        }

        if (hasRole(requester, "AGENT") && session.getAgent() != null && session.getAgent().getId().equals(requester.getId())) {
            return;
        }

        throw new ResourceNotFoundException("Ticket not found or access denied");
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private SupportSession findSessionOrThrow(Long sessionId) {
        return supportSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }

    private boolean hasRole(User user, String roleName) {
        String expected = normalizeRoleName(roleName);
        return user.getRoles().stream()
                .map(role -> normalizeRoleName(role.getName()))
                .anyMatch(expected::equals);
    }

    private String normalizeRoleName(String roleName) {
        String normalized = roleName == null ? "" : roleName.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized.substring(5) : normalized;
    }

    private SupportSessionResponseDTO mapToDTO(SupportSession session) {
        Long resolutionMinutes = null;
        if (session.getClosedAt() != null && session.getCreatedAt() != null) {
            resolutionMinutes = Duration.between(session.getCreatedAt(), session.getClosedAt()).toMinutes();
        }

        return SupportSessionResponseDTO.builder()
                .id(session.getId())
                .customerId(session.getCustomer().getId())
                .customerName(session.getCustomer().getFullName())
                .agentId(session.getAgent() != null ? session.getAgent().getId() : null)
                .agentName(session.getAgent() != null ? session.getAgent().getFullName() : null)
                .orderId(session.getOrder() != null ? session.getOrder().getId() : null)
                .orderTotal(session.getOrder() != null ? session.getOrder().getTotalAmount() : null)
                .orderStatus(session.getOrder() != null ? session.getOrder().getStatus().name() : null)
                .orderCreatedAt(session.getOrder() != null ? session.getOrder().getCreatedAt() : null)
                .subject(session.getSubject())
                .initialMessage(session.getInitialMessage())
                .status(session.getStatus())
                .resolvedById(session.getResolvedBy() != null ? session.getResolvedBy().getId() : null)
                .resolvedByName(session.getResolvedBy() != null ? session.getResolvedBy().getFullName() : null)
                .closedAt(session.getClosedAt())
                .resolutionMinutes(resolutionMinutes)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
}
