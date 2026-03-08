package com.modoria.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private boolean success;
    private String transactionId; // Stripe PaymentIntent ID
    private String status; // payment_intent status (e.g., succeeded, requires_action)
    private String clientSecret; // Needed for frontend confirmation if status is requires_action
    private String message;
    private String errorCode;

    public static PaymentResponseDTO success(String transactionId, String status, String message) {
        return PaymentResponseDTO.builder()
                .success(true)
                .transactionId(transactionId)
                .status(status)
                .message(message)
                .build();
    }

    public static PaymentResponseDTO failure(String message, String errorCode) {
        return PaymentResponseDTO.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
