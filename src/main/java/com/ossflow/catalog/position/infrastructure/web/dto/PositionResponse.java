package com.ossflow.catalog.position.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;

import java.time.Instant;

public record PositionResponse(
        Long id,
        String name,
        PositionType type,
        String description,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt
) {}
