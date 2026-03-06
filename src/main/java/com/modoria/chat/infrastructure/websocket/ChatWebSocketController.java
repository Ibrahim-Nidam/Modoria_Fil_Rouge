package com.modoria.chat.infrastructure.websocket;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.dto.ChatMessageRequest;
import com.modoria.chat.application.service.ChatService;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

        private final ChatService chatService;
        private final SimpMessagingTemplate messagingTemplate;
        private final UserRepository userRepository;

        @MessageMapping("/chat")
        public void processMessage(@Payload ChatMessageRequest chatMessageRequest, Principal principal) {
                if (principal == null) {
                        throw new IllegalStateException("User must be authenticated to send messages");
                }

                User sender = userRepository.findByEmail(principal.getName())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with email: " + principal.getName()));

                ChatMessageDTO savedMessage = chatService.sendMessage(sender.getId(), chatMessageRequest);

                // Send to receiver
                User receiver = userRepository.findById(chatMessageRequest.getReceiverId())
                                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

                // Destination: /user/{email}/queue/messages
                messagingTemplate.convertAndSendToUser(
                                receiver.getEmail(),
                                "/queue/messages",
                                savedMessage);
        }
}
