package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record Account(
        Long id,
        String email,
        String passwordHash,
        AccountProvider provider,
        String providerId,
        boolean emailVerified,
        int tokenVersion,
        Instant createdAt,
        Instant updatedAt
) {}
