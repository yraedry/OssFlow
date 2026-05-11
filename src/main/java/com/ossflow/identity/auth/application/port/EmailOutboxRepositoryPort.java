package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.EmailOutboxEntry;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EmailOutboxRepositoryPort {
    EmailOutboxEntry save(EmailOutboxEntry entry);
    Optional<EmailOutboxEntry> findById(Long id);
    List<EmailOutboxEntry> findRetriable(int maxAttempts, Instant retryBefore, int limit);
}
