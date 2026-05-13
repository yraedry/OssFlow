package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.EmailOutboxRepositoryPort;
import com.ossflow.identity.auth.domain.EmailOutboxEntry;
import com.ossflow.identity.auth.domain.EmailOutboxStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EmailOutboxService {

    private final EmailOutboxRepositoryPort outboxRepository;
    private final EmailService emailService;

    public EmailOutboxService(EmailOutboxRepositoryPort outboxRepository, EmailService emailService) {
        this.outboxRepository = outboxRepository;
        this.emailService = emailService;
    }

    @Transactional
    public EmailOutboxEntry enqueueVerification(Long accountId, String recipient, String rawToken) {
        return outboxRepository.save(new EmailOutboxEntry(
                null, accountId, recipient,
                emailService.verificationSubject(),
                emailService.verificationBody(rawToken),
                null,
                EmailOutboxStatus.PENDING,
                0, null, null, null, null
        ));
    }

    @Transactional
    public EmailOutboxEntry enqueuePasswordReset(Long accountId, String recipient, String rawToken) {
        return outboxRepository.save(new EmailOutboxEntry(
                null, accountId, recipient,
                emailService.passwordResetSubject(),
                emailService.passwordResetBody(rawToken),
                null,
                EmailOutboxStatus.PENDING,
                0, null, null, null, null
        ));
    }

    @Transactional
    public EmailOutboxEntry enqueueCoachingEmail(Long accountId, String recipient, String subject, String bodyHtml) {
        return outboxRepository.save(new EmailOutboxEntry(
                null, accountId, recipient,
                subject,
                bodyHtml,
                null,
                EmailOutboxStatus.PENDING,
                0, null, null, Instant.now(), null
        ));
    }
}
