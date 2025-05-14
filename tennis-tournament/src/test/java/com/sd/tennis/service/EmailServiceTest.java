package com.sd.tennis.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    EmailService emailService;

    @Test
    void sendRegistrationStatusEmail_approved() {
        String to = "user@example.com";
        String playerName = "John Doe";
        String tourney = "Open Championship";
        Integer regId = 123;

        emailService.sendRegistrationStatusEmail(to, playerName, tourney, regId, true);

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertThat(msg.getTo()).containsExactly(to);
        assertThat(msg.getSubject())
                .isEqualTo("Your Registration #123 for Open Championship: APPROVED");

        String text = msg.getText();
        // check greeting and that the body mentions the approved status
        assertThat(text).contains("Hello John Doe,");
        assertThat(text.toLowerCase())
                .contains("your registration (#123) for the tournament 'open championship' has been approved");
    }

    @Test
    void sendRegistrationStatusEmail_denied() {
        String to = "user@example.com";
        String playerName = "Jane Smith";
        String tourney = "City Cup";
        Integer regId = 456;

        emailService.sendRegistrationStatusEmail(to, playerName, tourney, regId, false);

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertThat(msg.getSubject())
                .isEqualTo("Your Registration #456 for City Cup: DENIED");

        String text = msg.getText();
        assertThat(text).contains("Hello Jane Smith,");
        assertThat(text.toLowerCase())
                .contains("has been denied");
    }
}
