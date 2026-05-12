package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.EmailOutboxRepositoryPort;
import com.ossflow.identity.auth.domain.EmailOutboxEntry;
import com.ossflow.identity.auth.domain.EmailOutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class EmailOutboxJob {

    static final int MAX_ATTEMPTS = 3;
    static final Duration BACKOFF = Duration.ofMinutes(5);
    static final int BATCH = 25;

    private final EmailOutboxRepositoryPort outboxRepository;
    private final EmailService emailService;
    private final Clock clock;

    public EmailOutboxJob(EmailOutboxRepositoryPort outboxRepository,
                          EmailService emailService,
                          Clock clock) {
        this.outboxRepository = outboxRepository;
        this.emailService = emailService;
        this.clock = clock;
    }

    @Scheduled(fixedDelay = 60_000L)
    public void process() {
        runOnce();
    }

    public int runOnce() {
        Instant now = Instant.now(clock);
        Instant retryBefore = now.minus(BACKOFF);
        List<EmailOutboxEntry> batch = outboxRepository.findRetriable(MAX_ATTEMPTS, retryBefore, BATCH);
        int sent = 0;
        for (EmailOutboxEntry entry : batch) {
            if (deliver(entry, now)) {
                sent++;
            }
        }
        return sent;
    }

    private boolean deliver(EmailOutboxEntry entry, Instant now) {
        try {
            emailService.send(entry.recipient(), entry.subject(), entry.bodyHtml());
            outboxRepository.save(new EmailOutboxEntry(
                    entry.id(), entry.accountId(), entry.recipient(), entry.subject(),
                    entry.bodyHtml(), entry.bodyText(), EmailOutboxStatus.SENT,
                    entry.attempts() + 1, now, null, entry.createdAt(), now
            ));
            return true;
        } catch (RuntimeException ex) {
            int attempts = entry.attempts() + 1;
            EmailOutboxStatus next = attempts >= MAX_ATTEMPTS ? EmailOutboxStatus.FAILED : EmailOutboxStatus.PENDING;
            log.warn("Email outbox delivery failed (id={}, attempts={}/{}, status={}): {}",
                    entry.id(), attempts, MAX_ATTEMPTS, next, ex.getMessage());
            outboxRepository.save(new EmailOutboxEntry(
                    entry.id(), entry.accountId(), entry.recipient(), entry.subject(),
                    entry.bodyHtml(), entry.bodyText(), next,
                    attempts, now, truncate(ex.getMessage()), entry.createdAt(), null
            ));
            return false;
        }
    }

    private static String truncate(String s) {
        if (s == null) return null;
        return s.length() > 2000 ? s.substring(0, 2000) : s;
    }
}
