package com.modoria.chat.domain.repository;

import com.modoria.chat.domain.enums.MessageStatus;
import com.modoria.chat.domain.model.ChatMessage;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .fullName("User One")
                .email("user1@example.com")
                .password("password123")
                .build();
        user2 = User.builder()
                .fullName("User Two")
                .email("user2@example.com")
                .password("password123")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void saveAndFindChatHistory_Success() {
        ChatMessage message1 = ChatMessage.builder()
                .sender(user1)
                .receiver(user2)
                .content("Hello user2")
                .build();

        ChatMessage message2 = ChatMessage.builder()
                .sender(user2)
                .receiver(user1)
                .content("Hi user1")
                .build();

        chatMessageRepository.save(message1);
        chatMessageRepository.save(message2);

        List<ChatMessage> history = chatMessageRepository.findChatHistory(user1.getId(), user2.getId());

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getContent()).isEqualTo("Hello user2");
        assertThat(history.get(1).getContent()).isEqualTo("Hi user1");
        assertThat(history.get(0).getStatus()).isEqualTo(MessageStatus.SENT);
    }

    @Test
    void countUnreadMessages_Success() {
        ChatMessage message = ChatMessage.builder()
                .sender(user1)
                .receiver(user2)
                .content("Unread message")
                .status(MessageStatus.SENT)
                .build();

        chatMessageRepository.save(message);

        long unreadCount = chatMessageRepository.countByReceiverIdAndStatus(user2.getId(), MessageStatus.SENT);
        assertThat(unreadCount).isEqualTo(1);
    }
}
