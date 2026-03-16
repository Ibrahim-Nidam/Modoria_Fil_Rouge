package com.modoria.payment.application.service;

import com.modoria.order.domain.model.Order;
import com.modoria.payment.application.dto.CheckoutSessionResponseDTO;

public interface PaymentService {
    CheckoutSessionResponseDTO createCheckoutSession(Order order, String successUrl, String cancelUrl);
}
