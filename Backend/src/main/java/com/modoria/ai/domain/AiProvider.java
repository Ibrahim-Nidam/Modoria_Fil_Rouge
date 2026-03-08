package com.modoria.ai.domain;

import com.modoria.ai.application.dto.AiChatResponseDTO;

public interface AiProvider {
    AiChatResponseDTO generateResponse(String prompt);
}
