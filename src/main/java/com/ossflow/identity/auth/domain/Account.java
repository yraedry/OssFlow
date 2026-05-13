package com.ossflow.identity.auth.domain;

import lombok.Builder;
import java.time.Instant;

@Builder(toBuilder = true)
public record Account(
        Long id,
        String email,
        String passwordHash,
        AccountProvider provider,
        String providerId,
        boolean emailVerified,
        int tokenVersion,
        AccountRole role,
        Instant createdAt,
        Instant updatedAt
) {}
