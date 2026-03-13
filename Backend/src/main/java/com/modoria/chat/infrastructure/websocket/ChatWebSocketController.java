package com.modoria.chat.infrastructure.websocket;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.dto.ChatMessageRequest;
import com.modoria.chat.application.service.ChatService;
import com.modoria.chat.application.service.SupportSessionService;
import com.modoria.ai.application.service.AiShoppingAssistantService;
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
        private final SupportSessionService supportSessionService;
        private final AiShoppingAssistantService aiAssistantService;

        @MessageMapping("/chat")
        public void processMessage(@Payload ChatMessageRequest chatMessageRequest, Principal principal) {
                if (principal == null) {
                        throw new IllegalStateException("User must be authenticated to send messages");
                }

                User sender = userRepository.findByEmail(principal.getName())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with email: " + principal.getName()));

                // Check if this is a support-bound message (receiver is likely an agent)
                // For now, if the session is BOT_HANDLING, the AI intercepts
                if (supportSessionService.isBotHandling(sender.getId())) {
                        handleBotInteraction(sender, chatMessageRequest);
                } else {
                        handleHumanInteraction(sender, chatMessageRequest);
                }
        }

        private void handleBotInteraction(User sender, ChatMessageRequest request) {
                // 1. Save user's message to history
                // Find or assume an "Assistant" user for the receiverId in the DB
                chatService.sendMessage(sender.getId(), request);

                // 2. Get AI response
                com.modoria.ai.application.dto.AiChatRequestDTO aiRequest = new com.modoria.ai.application.dto.AiChatRequestDTO();
                aiRequest.setMessage(request.getContent());

                com.modoria.ai.application.dto.AiChatResponseDTO aiResponse = aiAssistantService.chat(aiRequest);

                // 3. Save AI message to history
                // We'll use a virtual receiver/sender for the bot
                User assistant = userRepository.findByEmail("assistant@modoria.com")
                                .orElseGet(() -> {
                                        User bot = User.builder()
                                                        .fullName("Modoria AI Assistant")
                                                        .email("assistant@modoria.com")
                                                        .password("system_generated")
                                                        .enabled(true)
                                                        .build();
                                        return userRepository.save(bot);
                                });

                ChatMessageRequest botMsgRequest = new ChatMessageRequest();
                botMsgRequest.setContent(aiResponse.getResponse());
                botMsgRequest.setReceiverId(sender.getId());

                ChatMessageDTO botMsgDTO = chatService.sendMessage(assistant.getId(), botMsgRequest);

                // 4. Send back to user
                messagingTemplate.convertAndSendToUser(
                                sender.getEmail(),
                                "/queue/messages",
                                botMsgDTO);

                // 5. Handle handover if triggered
                if (aiResponse.isHandoverTriggered()) {
                        supportSessionService.switchToAgent(sender.getId());

                        // Notify user about the transition
                        ChatMessageDTO transitionMsg = ChatMessageDTO.builder()
                                        .content("SYSTEM: Scaling support... A human agent will be with you shortly.")
                                        .senderName("System")
                                        .timestamp(java.time.LocalDateTime.now())
                                        .build();

                        messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue/messages", transitionMsg);
                }
        }

        private void handleHumanInteraction(User sender, ChatMessageRequest request) {
                if (request.getReceiverId() == null) {
                        boolean isCustomer = sender.getRoles().stream().anyMatch(r -> r.getName().equals("CLIENT"));
                        if (isCustomer) {
                                com.modoria.chat.domain.model.SupportSession session = supportSessionService
                                                .getOrCreateSession(sender.getId());
                                if (session.getAgent() != null) {
                                        request.setReceiverId(session.getAgent().getId());
                                } else {
                                        // Agent not assigned yet, cannot send directly
                                        messagingTemplate.convertAndSendToUser(
                                                        sender.getEmail(),
                                                        "/queue/messages",
                                                        ChatMessageDTO.builder()
                                                                        .content("SYSTEM: Please wait, an agent has not been assigned to your session yet.")
                                                                        .senderName("System")
                                                                        .timestamp(java.time.LocalDateTime.now())
                                                                        .build());
                                        return;
                                }
                        }
                }

                if (request.getReceiverId() == null) {
                        throw new IllegalArgumentException("Receiver ID is missing");
                }

                ChatMessageDTO savedMessage = chatService.sendMessage(sender.getId(), request);

                User receiver = userRepository.findById(request.getReceiverId())
                                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

                messagingTemplate.convertAndSendToUser(
                                receiver.getEmail(),
                                "/queue/messages",
                                savedMessage);
        }
}
