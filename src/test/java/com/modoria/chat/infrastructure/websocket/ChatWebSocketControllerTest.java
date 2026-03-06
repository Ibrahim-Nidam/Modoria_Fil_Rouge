package com.modoria.chat.infrastructure.websocket;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.dto.ChatMessageRequest;
import com.modoria.chat.application.service.ChatService;
import com.modoria.chat.domain.enums.MessageStatus;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatWebSocketControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatWebSocketController chatWebSocketController;

    private User sender;
    private User receiver;
    private Principal principal;

    @BeforeEach
    void setUp() {
        sender = User.builder().id(1L).email("sender@test.com").fullName("Sender").build();
        receiver = User.builder().id(2L).email("receiver@test.com").fullName("Receiver").build();
        principal = mock(Principal.class);
    }

    @Test
    void processMessage_Success() {
        when(principal.getName()).thenReturn("sender@test.com");
        ChatMessageRequest request = ChatMessageRequest.builder()
                .receiverId(2L)
                .content("Hello")
                .build();

        ChatMessageDTO savedMessage = ChatMessageDTO.builder()
                .id(1L)
                .senderId(1L)
                .receiverId(2L)
                .content("Hello")
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();

        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(chatService.sendMessage(eq(1L), any(ChatMessageRequest.class))).thenReturn(savedMessage);

        chatWebSocketController.processMessage(request, principal);

        verify(chatService).sendMessage(eq(1L), any(ChatMessageRequest.class));
        verify(messagingTemplate).convertAndSendToUser(
                eq("receiver@test.com"),
                eq("/queue/messages"),
                eq(savedMessage));
    }

    @Test
    void processMessage_PrincipalNull_ThrowsException() {
        ChatMessageRequest request = ChatMessageRequest.builder().receiverId(2L).content("Hello").build();

        assertThatThrownBy(() -> chatWebSocketController.processMessage(request, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User must be authenticated to send messages");
    }

    @Test
    void processMessage_SenderNotFound_ThrowsException() {
        when(principal.getName()).thenReturn("sender@test.com");
        ChatMessageRequest request = ChatMessageRequest.builder().receiverId(2L).content("Hello").build();
        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatWebSocketController.processMessage(request, principal))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
