package com.modoria.payment.application.service;

import com.modoria.payment.application.dto.PaymentRequestDTO;
import com.modoria.payment.application.dto.PaymentResponseDTO;

public interface PaymentService {
    PaymentResponseDTO processPayment(PaymentRequestDTO request);
}
