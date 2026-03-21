package com.modoria.chat.application.service;

import com.modoria.chat.application.dto.OpenSupportTicketRequestDTO;
import com.modoria.chat.domain.repository.SupportSessionRepository;
import com.modoria.identity.domain.model.Role;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.order.domain.repository.OrderRepository;
import com.modoria.shared.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportSessionServiceTest {

    @Mock
    private SupportSessionRepository supportSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatService chatService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private SupportSessionService service;

    @Test
    void openTicket_whenRequesterIsAgent_throwsBadRequest() {
        User agent = User.builder()
                .id(3L)
                .roles(Set.of(Role.builder().name("AGENT").build()))
                .build();

        OpenSupportTicketRequestDTO request = OpenSupportTicketRequestDTO.builder()
                .orderId(1L)
                .subject("Need help")
                .message("Please check")
                .build();

        when(userRepository.findById(3L)).thenReturn(Optional.of(agent));

        assertThatThrownBy(() -> service.openTicket(3L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Agents cannot open support tickets");
    }
}
