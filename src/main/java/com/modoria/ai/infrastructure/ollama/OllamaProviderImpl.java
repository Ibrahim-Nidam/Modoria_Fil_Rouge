package com.modoria.ai.infrastructure.ollama;

import com.modoria.ai.application.dto.AiChatResponseDTO;
import com.modoria.ai.domain.AiProvider;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "ollama")
public class OllamaProviderImpl implements AiProvider {

    private final ChatModel chatModel;

    public OllamaProviderImpl(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public AiChatResponseDTO generateResponse(String prompt) {
        String response = chatModel.call(prompt);
        return AiChatResponseDTO.builder()
                .response(response)
                .build();
    }
}
