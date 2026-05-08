package com.ossflow.catalog.technique.domain;

import com.ossflow.catalog.position.domain.Visibility;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Technique(
        Long id,
        Long ownerId,
        String name,
        TechniqueCategory category,
        TechniqueFamily family,
        String description,
        String youtubeUrl,
        Belt minimumBelt,
        Modality modality,
        Long startPositionId,
        Long endPositionId,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
