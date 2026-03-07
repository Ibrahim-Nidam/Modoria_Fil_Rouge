package com.modoria.ai.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponseDTO {
    private String response;
    private boolean handoverTriggered;
    private List<ProductRecommendation> recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRecommendation {
        private Long id;
        private String name;
        private Double price;
        private String reason;
    }
}
