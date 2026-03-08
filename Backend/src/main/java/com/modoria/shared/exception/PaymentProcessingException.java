package com.modoria.shared.exception;

import org.springframework.http.HttpStatus;

public class PaymentProcessingException extends AppException {
    public PaymentProcessingException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED);
    }
}
