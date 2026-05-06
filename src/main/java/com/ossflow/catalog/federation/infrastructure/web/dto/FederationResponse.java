package com.ossflow.catalog.federation.infrastructure.web.dto;

import java.time.Instant;

public record FederationResponse(
        Long id,
        String code,
        String name,
        String officialUrl,
        Instant createdAt,
        Instant updatedAt
) {}
