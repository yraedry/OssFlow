package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record EmailVerificationToken(
        Long id,
        Long accountId,
        String tokenHash,
        Instant expiresAt,
        Instant usedAt
) {}
