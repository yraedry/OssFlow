package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record EmailOutboxEntry(
        Long id,
        Long accountId,
        String recipient,
        String subject,
        String bodyHtml,
        String bodyText,
        EmailOutboxStatus status,
        int attempts,
        Instant lastAttemptAt,
        String lastError,
        Instant createdAt,
        Instant sentAt
) {}
