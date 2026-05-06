package com.ossflow.journal.tag.infrastructure.web.dto;

import java.time.Instant;

public record TagResponse(
        Long id,
        String name,
        Instant createdAt
) {}
