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
}
