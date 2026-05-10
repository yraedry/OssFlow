package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record RefreshToken(
        Long id,
        Long accountId,
        String tokenHash,
        int tokenVersion,
        Instant expiresAt,
        Instant createdAt,
        Instant revokedAt
) {}
