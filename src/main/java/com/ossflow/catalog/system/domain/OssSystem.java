package com.ossflow.catalog.system.domain;

import com.ossflow.catalog.position.domain.Visibility;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record OssSystem(
        Long id,
        Long ownerId,
        String name,
        String description,
        Long anchorPositionId,
        String flowDefinition,
        String flowSchemaVersion,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
