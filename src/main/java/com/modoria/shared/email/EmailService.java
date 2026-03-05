package com.modoria.shared.email;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
