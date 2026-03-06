package com.modoria.chat.application.service;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.dto.ChatMessageRequest;
import com.modoria.chat.domain.enums.MessageStatus;
import com.modoria.chat.domain.model.ChatMessage;
import com.modoria.chat.domain.repository.ChatMessageRepository;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = User.builder().id(1L).fullName("Sender").email("sender@test.com").build();
        receiver = User.builder().id(2L).fullName("Receiver").email("receiver@test.com").build();
    }

    @Test
    void sendMessage_Success() {
        ChatMessageRequest request = ChatMessageRequest.builder()
                .receiverId(2L)
                .content("Hello")
                .build();

        ChatMessage savedMessage = ChatMessage.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .content("Hello")
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(savedMessage);

        ChatMessageDTO result = chatService.sendMessage(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello");
        assertThat(result.getSenderId()).isEqualTo(1L);
        assertThat(result.getReceiverId()).isEqualTo(2L);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void sendMessage_ReceiverNotFound() {
        ChatMessageRequest request = ChatMessageRequest.builder()
                .receiverId(99L)
                .content("Hello")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.sendMessage(1L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getChatHistory_Success() {
        ChatMessage message = ChatMessage.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .content("Old message")
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.READ)
                .build();

        when(chatMessageRepository.findChatHistory(1L, 2L)).thenReturn(List.of(message));

        List<ChatMessageDTO> result = chatService.getChatHistory(1L, 2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Old message");
    }
}
