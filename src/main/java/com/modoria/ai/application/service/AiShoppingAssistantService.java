package com.modoria.ai.application.service;

import com.modoria.ai.application.dto.AiChatRequestDTO;
import com.modoria.ai.application.dto.AiChatResponseDTO;

public interface AiShoppingAssistantService {
    AiChatResponseDTO chat(AiChatRequestDTO request);
}
