package com.modoria.ai.application.service;

import com.modoria.ai.application.dto.AiChatRequestDTO;
import com.modoria.ai.application.dto.AiChatResponseDTO;
import com.modoria.ai.domain.AiProvider;
import com.modoria.catalog.application.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiShoppingAssistantServiceImpl implements AiShoppingAssistantService {

    private final AiProvider aiProvider;
    private final ProductService productService;

    @Override
    public AiChatResponseDTO chat(AiChatRequestDTO request) {
        String catalogSummary = productService.getCatalogSummary();

        String systemPrompt = "You are the Modoria Shopping Assistant, an intelligent fashion expert. \n" +
                "You help customers find products from our catalog. \n\n" +
                "GUIDELINES:\n" +
                "1. If you can help the user with the catalog, provide a friendly and professional response.\n" +
                "2. Mention product names and prices exactly as they appear in the catalog.\n" +
                "3. If the user asks for something we don't have, or if the user seems frustrated, or if the user explicitly asks for a human/person/agent, you MUST include the token [HANDOVER] at the beginning of your response.\n"
                +
                "4. When [HANDOVER] is triggered, explain to the user that you are transferring them to a human specialist.\n\n"
                +
                "CURRENT CATALOG:\n" +
                catalogSummary + "\n\n" +
                "User query: " + request.getMessage();

        AiChatResponseDTO responseDTO = aiProvider.generateResponse(systemPrompt);

        if (responseDTO.getResponse() != null && responseDTO.getResponse().contains("[HANDOVER]")) {
            responseDTO.setHandoverTriggered(true);
            responseDTO.setResponse(responseDTO.getResponse().replace("[HANDOVER]", "").trim());
        }

        return responseDTO;
    }
}
