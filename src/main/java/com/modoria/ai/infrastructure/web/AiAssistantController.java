package com.modoria.ai.infrastructure.web;

import com.modoria.ai.application.dto.AiChatRequestDTO;
import com.modoria.ai.application.dto.AiChatResponseDTO;
import com.modoria.ai.application.service.AiShoppingAssistantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiAssistantController {

    private final AiShoppingAssistantService aiShoppingAssistantService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponseDTO> chat(@Valid @RequestBody AiChatRequestDTO request) {
        return ResponseEntity.ok(aiShoppingAssistantService.chat(request));
    }
}
