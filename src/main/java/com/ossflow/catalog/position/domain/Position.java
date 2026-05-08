package com.ossflow.catalog.position.domain;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Position(
        Long id,
        Long ownerId,
        String name,
        PositionType type,
        String description,
        String youtubeUrl,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
