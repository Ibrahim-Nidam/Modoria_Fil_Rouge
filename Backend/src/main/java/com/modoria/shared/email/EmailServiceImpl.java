package com.modoria.shared.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        log.info("Sending password reset email to {} via Mailtrap", toEmail);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@modoria.com");
        message.setTo(toEmail);
        message.setSubject("Modoria - Password Reset Request");
        message.setText("Hello,\n\n" +
                "You have requested to reset your password. Please use the following token to proceed:\n\n" +
                resetToken + "\n\n" +
                "This token will expire in 30 minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\nModoria Team");

        mailSender.send(message);
        log.info("Password reset email successfully sent.");
    }

    @Override
    public void sendOrderConfirmationEmail(String toEmail, com.modoria.order.domain.model.Order order,
            byte[] pdfInvoice) {
        log.info("Sending order confirmation email to {} via Mailtrap for order {}", toEmail, order.getId());

        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
                    message, true);

            helper.setFrom("noreply@modoria.com");
            helper.setTo(toEmail);
            helper.setSubject("Modoria - Order Confirmation #" + order.getId());

            String text = String.format("Hello,\n\n" +
                    "Thank you for your purchase!\n" +
                    "Your order #%d has been confirmed. The total amount is $%.2f.\n\n" +
                    "Please find your invoice attached as a PDF.\n\n" +
                    "Regards,\nModoria Team", order.getId(), order.getTotalAmount());

            helper.setText(text);

            // Attach PDF
            helper.addAttachment("Invoice_" + order.getId() + ".pdf",
                    new org.springframework.core.io.ByteArrayResource(pdfInvoice));

            mailSender.send(message);
            log.info("Order confirmation email successfully sent.");

        } catch (jakarta.mail.MessagingException e) {
            log.error("Failed to send order confirmation email to {}", toEmail, e);
            throw new RuntimeException("Failed to send order confirmation email", e);
        }
    }
}
