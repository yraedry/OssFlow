package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.EmailOutboxRepositoryPort;
import com.ossflow.identity.auth.domain.EmailOutboxEntry;
import com.ossflow.identity.auth.domain.EmailOutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailOutboxJobTest {

    private EmailOutboxRepositoryPort repository;
    private EmailService emailService;
    private EmailOutboxJob job;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        repository = mock(EmailOutboxRepositoryPort.class);
        emailService = mock(EmailService.class);
        fixedClock = Clock.fixed(Instant.parse("2026-05-11T10:00:00Z"), ZoneOffset.UTC);
        job = new EmailOutboxJob(repository, emailService, fixedClock);
        when(repository.save(any(EmailOutboxEntry.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private EmailOutboxEntry pending(long id, int attempts) {
        return new EmailOutboxEntry(id, 1L, "u@e.com", "subj", "<b>html</b>", null,
                EmailOutboxStatus.PENDING, attempts, null, null, Instant.now(fixedClock), null);
    }

    @Test
    void successful_delivery_marks_entry_SENT_and_sets_sentAt() {
        when(repository.findRetriable(eq(EmailOutboxJob.MAX_ATTEMPTS), any(), anyInt()))
                .thenReturn(List.of(pending(1L, 0)));
        doNothing().when(emailService).send(any(), any(), any());

        int sent = job.runOnce();

        assertThat(sent).isEqualTo(1);
        ArgumentCaptor<EmailOutboxEntry> captor = ArgumentCaptor.forClass(EmailOutboxEntry.class);
        verify(repository).save(captor.capture());
        EmailOutboxEntry saved = captor.getValue();
        assertThat(saved.status()).isEqualTo(EmailOutboxStatus.SENT);
        assertThat(saved.attempts()).isEqualTo(1);
        assertThat(saved.sentAt()).isEqualTo(Instant.now(fixedClock));
        assertThat(saved.lastError()).isNull();
    }

    @Test
    void failed_delivery_below_max_keeps_PENDING_and_records_attempt() {
        when(repository.findRetriable(eq(EmailOutboxJob.MAX_ATTEMPTS), any(), anyInt()))
                .thenReturn(List.of(pending(2L, 0)));
        doThrow(new EmailDeliveryException("smtp down", new RuntimeException()))
                .when(emailService).send(any(), any(), any());

        int sent = job.runOnce();

        assertThat(sent).isZero();
        ArgumentCaptor<EmailOutboxEntry> captor = ArgumentCaptor.forClass(EmailOutboxEntry.class);
        verify(repository).save(captor.capture());
        EmailOutboxEntry saved = captor.getValue();
        assertThat(saved.status()).isEqualTo(EmailOutboxStatus.PENDING);
        assertThat(saved.attempts()).isEqualTo(1);
        assertThat(saved.lastAttemptAt()).isEqualTo(Instant.now(fixedClock));
        assertThat(saved.lastError()).contains("smtp down");
        assertThat(saved.sentAt()).isNull();
    }

    @Test
    void failed_delivery_reaching_max_attempts_marks_FAILED() {
        // attempts en BD = 2, este intento será el 3º (= MAX_ATTEMPTS)
        when(repository.findRetriable(eq(EmailOutboxJob.MAX_ATTEMPTS), any(), anyInt()))
                .thenReturn(List.of(pending(3L, 2)));
        doThrow(new EmailDeliveryException("smtp down again", new RuntimeException()))
                .when(emailService).send(any(), any(), any());

        job.runOnce();

        ArgumentCaptor<EmailOutboxEntry> captor = ArgumentCaptor.forClass(EmailOutboxEntry.class);
        verify(repository).save(captor.capture());
        EmailOutboxEntry saved = captor.getValue();
        assertThat(saved.status()).isEqualTo(EmailOutboxStatus.FAILED);
        assertThat(saved.attempts()).isEqualTo(3);
    }

    @Test
    void empty_batch_does_not_call_email_service() {
        when(repository.findRetriable(anyInt(), any(), anyInt())).thenReturn(List.of());

        int sent = job.runOnce();

        assertThat(sent).isZero();
        verify(emailService, org.mockito.Mockito.never()).send(any(), any(), any());
    }
}
