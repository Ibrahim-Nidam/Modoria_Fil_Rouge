package com.modoria.exception;

import org.springframework.http.HttpStatus;

public class PaymentProcessingException extends AppException{
    public PaymentProcessingException(String message){
        super(message, HttpStatus.BAD_REQUEST);
    }
}
