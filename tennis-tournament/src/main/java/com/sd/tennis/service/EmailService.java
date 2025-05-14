package com.sd.tennis.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationStatusEmail(
            String to,
            String playerName,
            String tournamentName,
            Integer registrationId,
            boolean approved) {

        String status = approved ? "APPROVED" : "DENIED";
        String subject = String.format(
                "Your Registration #%d for %s: %s",
                registrationId,
                tournamentName,
                status
        );

        String text = String.join("\n",
                String.format("Hello %s,", playerName),
                "",  // blank line
                String.format("We are writing to inform you that your registration (#%d) for the tournament '%s' has been %s.",
                        registrationId,
                        tournamentName,
                        status.toLowerCase()),
                "",
                "If you have any questions, please reply to this email.",
                "",
                "Good luck and see you at the tournament!",
                "",
                "Best regards,",
                "Tournament Administration Team"
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}

