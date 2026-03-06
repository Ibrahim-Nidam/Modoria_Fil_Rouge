package com.modoria.chat.infrastructure.web;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.service.ChatService;
import com.modoria.chat.domain.enums.MessageStatus;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.identity.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(username = "user@test.com")
    void getChatHistory_Success() throws Exception {
        User currentUser = User.builder().id(1L).email("user@test.com").build();
        ChatMessageDTO message = ChatMessageDTO.builder()
                .id(1L)
                .senderId(1L)
                .receiverId(2L)
                .content("History message")
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(chatService.getChatHistory(anyLong(), anyLong())).thenReturn(List.of(message));

        mockMvc.perform(get("/api/v1/chat/2")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("History message"));
    }
}
