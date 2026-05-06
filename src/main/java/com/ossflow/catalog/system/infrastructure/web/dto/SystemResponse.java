package com.ossflow.catalog.system.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.Visibility;

import java.time.Instant;

public record SystemResponse(
        Long id,
        String name,
        String description,
        Long anchorPositionId,
        String flowDefinition,
        String flowSchemaVersion,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
