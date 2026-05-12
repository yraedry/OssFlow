package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record PasswordResetToken(
        Long id,
        Long accountId,
        String tokenHash,
        Instant expiresAt,
        Instant usedAt
) {}
