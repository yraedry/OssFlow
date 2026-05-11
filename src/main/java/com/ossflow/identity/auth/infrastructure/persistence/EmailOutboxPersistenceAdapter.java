package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.EmailOutboxRepositoryPort;
import com.ossflow.identity.auth.domain.EmailOutboxEntry;
import com.ossflow.identity.auth.domain.EmailOutboxStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class EmailOutboxPersistenceAdapter implements EmailOutboxRepositoryPort {

    private final EmailOutboxJpaRepository jpaRepository;

    public EmailOutboxPersistenceAdapter(EmailOutboxJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EmailOutboxEntry save(EmailOutboxEntry entry) {
        EmailOutboxEntity entity = EmailOutboxEntity.builder()
                .id(entry.id())
                .accountId(entry.accountId())
                .recipient(entry.recipient())
                .subject(entry.subject())
                .bodyHtml(entry.bodyHtml())
                .bodyText(entry.bodyText())
                .status(entry.status() == null ? EmailOutboxStatus.PENDING : entry.status())
                .attempts(entry.attempts())
                .lastAttemptAt(entry.lastAttemptAt())
                .lastError(entry.lastError())
                .createdAt(entry.createdAt())
                .sentAt(entry.sentAt())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<EmailOutboxEntry> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<EmailOutboxEntry> findRetriable(int maxAttempts, Instant retryBefore, int limit) {
        return jpaRepository.findRetriable(
                EmailOutboxStatus.PENDING, EmailOutboxStatus.FAILED,
                maxAttempts, retryBefore, PageRequest.of(0, limit)
        ).stream().map(this::toDomain).toList();
    }

    private EmailOutboxEntry toDomain(EmailOutboxEntity e) {
        return new EmailOutboxEntry(e.getId(), e.getAccountId(), e.getRecipient(),
                e.getSubject(), e.getBodyHtml(), e.getBodyText(),
                e.getStatus(), e.getAttempts(), e.getLastAttemptAt(),
                e.getLastError(), e.getCreatedAt(), e.getSentAt());
    }
}
