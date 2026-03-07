package com.modoria.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private BigDecimal amount;
    private String currency;
    private String orderReference;
    private String stripePaymentMethodId; // Token or ID from frontend
}
