package com.modoria.shared.email;

import com.modoria.order.domain.model.Order;
import com.modoria.order.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final DateTimeFormatter ORDER_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("fr-MA"));

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

                helper.setText(buildPasswordResetHtml(resetUrl), true);
            mailSender.send(message);
            log.info("Professional password reset email successfully sent.");

        } catch (MessagingException e) {
            log.error("Failed to send professional password reset email to {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendOrderConfirmationEmail(String toEmail, Order order) {
        log.info("Sending order confirmation email to {} via Mailtrap for order {}", toEmail, order.getId());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@modoria.com");
            helper.setTo(toEmail);
            helper.setSubject("Modoria - Order Confirmation #" + order.getId());

            helper.setText(buildOrderConfirmationHtml(order), true);

            mailSender.send(message);
            log.info("Order confirmation email successfully sent.");

        } catch (MessagingException e) {
            log.error("Failed to send order confirmation email to {}", toEmail, e);
            throw new RuntimeException("Failed to send order confirmation email", e);
        }
    }

    private String buildPasswordResetHtml(String resetUrl) {
        return "<div style=\"margin:0;padding:24px;background:#f6f2ec;font-family:Segoe UI,Arial,sans-serif;color:#2d241e;\">"
                + "<div style=\"max-width:680px;margin:0 auto;background:#ffffff;border:1px solid #e8dfd5;\">"
                + "<div style=\"padding:32px 36px;background:linear-gradient(135deg,#1f1713 0%,#4b352a 100%);color:#f8f4ef;\">"
                + "<p style=\"margin:0 0 10px;font-size:12px;letter-spacing:0.24em;text-transform:uppercase;color:#d7c1ad;\">Security Notice</p>"
                + "<h1 style=\"margin:0;font-size:30px;line-height:1.2;font-weight:700;\">Reset your Modoria password.</h1>"
                + "<p style=\"margin:14px 0 0;font-size:15px;line-height:1.7;color:#efe6de;\">We received a request to update the password for your account. Use the secure link below to choose a new one.</p>"
                + "</div>"
                + "<div style=\"padding:32px 36px;\">"
                + "<div style=\"padding:20px;background:#f8f4ef;border:1px solid #ebe2d8;\">"
                + "<p style=\"margin:0 0 12px;font-size:14px;line-height:1.7;color:#5c5148;\">For your security, this reset link expires in 30 minutes. If you did not request a password reset, you can safely ignore this message and your password will remain unchanged.</p>"
                + "<a href=\"" + resetUrl + "\" style=\"display:inline-block;padding:12px 22px;background:#2d241e;color:#ffffff;text-decoration:none;font-size:12px;font-weight:700;letter-spacing:0.12em;text-transform:uppercase;\">Reset Password</a>"
                + "</div>"
                + "<div style=\"margin-top:24px;display:flex;flex-wrap:wrap;gap:16px;\">"
                + infoCard("Link Validity", "30 Minutes")
                + infoCard("Action", "Choose a new password")
                + infoCard("Support", "Reply if you need help")
                + "</div>"
                + "</div>"
                + "<div style=\"padding:20px 36px;border-top:1px solid #ece7e1;font-size:12px;color:#8a7d71;background:#fcfaf8;\">"
                + "Modoria account security. If this request was not initiated by you, no further action is required."
                + "</div>"
                + "</div>"
                + "</div>";
    }

    private String buildOrderConfirmationHtml(Order order) {
        StringBuilder itemsHtml = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            itemsHtml.append("<tr>")
                    .append("<td style=\"padding:12px 0;border-bottom:1px solid #ece7e1;color:#2d241e;\">")
                    .append(escapeHtml(item.getProduct().getName()))
                    .append("</td>")
                    .append("<td style=\"padding:12px 0;border-bottom:1px solid #ece7e1;color:#6b625b;text-align:center;\">")
                    .append(item.getQuantity())
                    .append("</td>")
                    .append("<td style=\"padding:12px 0;border-bottom:1px solid #ece7e1;color:#2d241e;text-align:right;\">")
                    .append(formatCurrency(lineTotal))
                    .append("</td>")
                    .append("</tr>");
        }

        String orderDate = order.getCreatedAt() == null
                ? "Recently placed"
                : order.getCreatedAt().format(ORDER_DATE_FORMATTER);
        String orderUrl = frontendUrl + "/profile";

        return "<div style=\"margin:0;padding:24px;background:#f6f2ec;font-family:Segoe UI,Arial,sans-serif;color:#2d241e;\">"
                + "<div style=\"max-width:680px;margin:0 auto;background:#ffffff;border:1px solid #e8dfd5;\">"
                + "<div style=\"padding:32px 36px;background:linear-gradient(135deg,#1f1713 0%,#4b352a 100%);color:#f8f4ef;\">"
                + "<p style=\"margin:0 0 10px;font-size:12px;letter-spacing:0.24em;text-transform:uppercase;color:#d7c1ad;\">Order Confirmed</p>"
                + "<h1 style=\"margin:0;font-size:30px;line-height:1.2;font-weight:700;\">Thank you for shopping with Modoria.</h1>"
                + "<p style=\"margin:14px 0 0;font-size:15px;line-height:1.7;color:#efe6de;\">Your order has been successfully confirmed and is now being prepared.</p>"
                + "</div>"
                + "<div style=\"padding:32px 36px;\">"
                + "<div style=\"display:flex;flex-wrap:wrap;gap:16px;margin-bottom:28px;\">"
                + infoCard("Order Number", "#" + order.getId())
                + infoCard("Placed On", orderDate)
                + infoCard("Order Total", formatCurrency(order.getTotalAmount()))
                + infoCard("Status", order.getStatus().name())
                + "</div>"
                + "<h2 style=\"margin:0 0 14px;font-size:18px;color:#2d241e;\">Order Summary</h2>"
                + "<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">"
                + "<thead><tr>"
                + "<th style=\"padding:0 0 10px;text-align:left;color:#7b7067;font-size:12px;letter-spacing:0.12em;text-transform:uppercase;border-bottom:1px solid #d9cec3;\">Item</th>"
                + "<th style=\"padding:0 0 10px;text-align:center;color:#7b7067;font-size:12px;letter-spacing:0.12em;text-transform:uppercase;border-bottom:1px solid #d9cec3;\">Qty</th>"
                + "<th style=\"padding:0 0 10px;text-align:right;color:#7b7067;font-size:12px;letter-spacing:0.12em;text-transform:uppercase;border-bottom:1px solid #d9cec3;\">Line Total</th>"
                + "</tr></thead>"
                + "<tbody>" + itemsHtml + "</tbody>"
                + "</table>"
                + "<div style=\"margin-top:28px;padding:20px;background:#f8f4ef;border:1px solid #ebe2d8;\">"
                + "<p style=\"margin:0 0 12px;font-size:14px;line-height:1.7;color:#5c5148;\">You can review your order history and support tickets directly from your Modoria profile.</p>"
                + "<a href=\"" + orderUrl + "\" style=\"display:inline-block;padding:12px 22px;background:#2d241e;color:#ffffff;text-decoration:none;font-size:12px;font-weight:700;letter-spacing:0.12em;text-transform:uppercase;\">View My Orders</a>"
                + "</div>"
                + "</div>"
                + "<div style=\"padding:20px 36px;border-top:1px solid #ece7e1;font-size:12px;color:#8a7d71;background:#fcfaf8;\">"
                + "Modoria seasonal commerce. Need help? Reply to this email or open a support ticket from your profile."
                + "</div>"
                + "</div>"
                + "</div>";
    }

    private String infoCard(String label, String value) {
        return "<div style=\"flex:1 1 140px;min-width:140px;padding:16px;border:1px solid #ebe2d8;background:#fcfaf8;\">"
                + "<p style=\"margin:0 0 8px;font-size:11px;letter-spacing:0.14em;text-transform:uppercase;color:#8a7d71;\">" + label + "</p>"
                + "<p style=\"margin:0;font-size:15px;font-weight:600;color:#2d241e;\">" + escapeHtml(value) + "</p>"
                + "</div>";
    }

    private String formatCurrency(BigDecimal amount) {
        return CURRENCY_FORMATTER.format(amount == null ? BigDecimal.ZERO : amount);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
