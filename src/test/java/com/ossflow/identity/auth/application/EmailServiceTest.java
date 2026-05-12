package com.ossflow.identity.auth.application;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService service;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        given(mailSender.createMimeMessage()).willReturn(new MimeMessage(Session.getInstance(new Properties())));
        service = new EmailService(mailSender, "http://localhost:5173");
    }

    @Test
    void sendVerificationEmail_passes_through_mail_sender() {
        service.sendVerificationEmail("user@example.com", "raw-token-abc");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void send_propagates_failure_as_EmailDeliveryException() {
        willThrow(new MailSendException("smtp down"))
                .given(mailSender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> service.sendVerificationEmail("user@example.com", "token"))
                .isInstanceOf(EmailDeliveryException.class);
    }

    @Test
    void password_reset_email_also_propagates() {
        willThrow(new MailSendException("smtp down"))
                .given(mailSender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> service.sendPasswordResetEmail("user@example.com", "token"))
                .isInstanceOf(EmailDeliveryException.class);
    }

    @Test
    void send_returns_normally_when_no_exception() {
        assertThatCode(() -> service.sendVerificationEmail("ok@example.com", "t"))
                .doesNotThrowAnyException();
    }
}
