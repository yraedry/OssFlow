package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.EmailOutboxRepositoryPort;
import com.ossflow.identity.auth.domain.EmailOutboxEntry;
import com.ossflow.identity.auth.domain.EmailOutboxStatus;
import com.ossflow.shared.properties.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailOutboxServiceTest {

    private EmailOutboxRepositoryPort repository;
    private EmailService emailService;
    private EmailOutboxService service;

    @BeforeEach
    void setUp() {
        repository = mock(EmailOutboxRepositoryPort.class);
        emailService = new EmailService(mock(JavaMailSender.class),
                new AppProperties("http://localhost:5173",
                        new AppProperties.CookieProperties(false, "Lax", "/api/auth"),
                        new AppProperties.RefreshTokenProperties(2592000L)));
        service = new EmailOutboxService(repository, emailService);
        when(repository.save(org.mockito.ArgumentMatchers.any(EmailOutboxEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void enqueueVerification_persists_pending_entry_with_verification_subject() {
        service.enqueueVerification(7L, "user@example.com", "raw-token");

        ArgumentCaptor<EmailOutboxEntry> captor = ArgumentCaptor.forClass(EmailOutboxEntry.class);
        org.mockito.Mockito.verify(repository).save(captor.capture());
        EmailOutboxEntry saved = captor.getValue();
        assertThat(saved.status()).isEqualTo(EmailOutboxStatus.PENDING);
        assertThat(saved.attempts()).isZero();
        assertThat(saved.recipient()).isEqualTo("user@example.com");
        assertThat(saved.subject()).isEqualTo(emailService.verificationSubject());
        assertThat(saved.bodyHtml()).contains("raw-token");
        assertThat(saved.accountId()).isEqualTo(7L);
    }

    @Test
    void enqueuePasswordReset_persists_pending_entry_with_reset_subject() {
        service.enqueuePasswordReset(11L, "user@example.com", "reset-token");

        ArgumentCaptor<EmailOutboxEntry> captor = ArgumentCaptor.forClass(EmailOutboxEntry.class);
        org.mockito.Mockito.verify(repository).save(captor.capture());
        EmailOutboxEntry saved = captor.getValue();
        assertThat(saved.status()).isEqualTo(EmailOutboxStatus.PENDING);
        assertThat(saved.subject()).isEqualTo(emailService.passwordResetSubject());
        assertThat(saved.bodyHtml()).contains("reset-token");
        assertThat(saved.accountId()).isEqualTo(11L);
    }
}
