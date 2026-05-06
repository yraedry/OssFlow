package com.ossflow.catalog.federation.domain;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Federation(
        Long id,
        String code,
        String name,
        String officialUrl,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
