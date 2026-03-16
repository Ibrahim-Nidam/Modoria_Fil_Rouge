package com.modoria.shared.email;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String resetToken);

    void sendOrderConfirmationEmail(String toEmail, com.modoria.order.domain.model.Order order);
}
