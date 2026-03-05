package com.modoria.shared.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends AppException {
    public UnauthorizedAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
