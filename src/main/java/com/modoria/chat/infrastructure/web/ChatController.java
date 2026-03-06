package com.modoria.chat.infrastructure.web;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.service.ChatService;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
            @PathVariable Long userId,
            Principal principal) {

        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ChatMessageDTO> history = chatService.getChatHistory(currentUser.getId(), userId);
        return ResponseEntity.ok(history);
    }
}
