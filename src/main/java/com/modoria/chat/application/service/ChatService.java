package com.modoria.chat.application.service;

import com.modoria.chat.application.dto.ChatMessageDTO;
import com.modoria.chat.application.dto.ChatMessageRequest;
import com.modoria.chat.domain.enums.MessageStatus;
import com.modoria.chat.domain.model.ChatMessage;
import com.modoria.chat.domain.repository.ChatMessageRepository;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageDTO sendMessage(Long senderId, ChatMessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return mapToDTO(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatHistory(Long user1Id, Long user2Id) {
        return chatMessageRepository.findChatHistory(user1Id, user2Id)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ChatMessageDTO mapToDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getFullName())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .status(message.getStatus())
                .build();
    }
}
