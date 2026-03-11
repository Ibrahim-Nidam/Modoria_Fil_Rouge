package com.modoria.shared.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        log.info("Sending professional password reset email to {} via Mailtrap", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@modoria.com");
            helper.setTo(toEmail);
            helper.setSubject("Modoria - Reset Your Password");

            String resetUrl = String.format("%s/auth/reset-password?token=%s", frontendUrl, resetToken);

            String htmlContent = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 12px; background-color: #ffffff;\">" +
                    "  <div style=\"text-align: center; margin-bottom: 30px;\">" +
                    "    <h1 style=\"color: #2e7d32; margin: 0; font-size: 28px;\">Modoria</h1>" +
                    "    <p style=\"color: #666; margin: 5px 0 0;\">Your Seasonal Marketplace</p>" +
                    "  </div>" +
                    "  <div style=\"padding: 20px; background-color: #f9fbf9; border-radius: 8px;\">" +
                    "    <h2 style=\"color: #333; margin-top: 0;\">Password Reset Request</h2>" +
                    "    <p style=\"color: #555; line-height: 1.6;\">Hello,</p>" +
                    "    <p style=\"color: #555; line-height: 1.6;\">We received a request to reset the password for your Modoria account. Click the button below to choose a new password:</p>" +
                    "    <div style=\"text-align: center; margin: 30px 0;\">" +
                    "      <a href=\"" + resetUrl + "\" style=\"background-color: #4caf50; color: white; padding: 14px 28px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; box-shadow: 0 2px 4px rgba(0,0,0,0.1); transition: background-color 0.3s;\">Reset Password</a>" +
                    "    </div>" +
                    "    <p style=\"color: #777; font-size: 14px;\">This link will expire in 30 minutes for your security.</p>" +
                    "    <p style=\"color: #777; font-size: 14px;\">If you didn't request this change, you can safely ignore this email.</p>" +
                    "  </div>" +
                    "  <div style=\"margin-top: 30px; text-align: center; border-top: 1px solid #eee; padding-top: 20px;\">" +
                    "    <p style=\"color: #999; font-size: 12px; margin: 0;\">&copy; 2026 Modoria. All rights reserved.</p>" +
                    "    <p style=\"color: #999; font-size: 12px; margin: 5px 0 0;\">Inspired by the Seasons.</p>" +
                    "  </div>" +
                    "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Professional password reset email successfully sent.");

        } catch (MessagingException e) {
            log.error("Failed to send professional password reset email to {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
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
