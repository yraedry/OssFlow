package com.ossflow.catalog.exercise.domain;

import com.ossflow.catalog.position.domain.Visibility;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Exercise(
        Long id,
        Long ownerId,
        String name,
        String description,
        ExerciseCategory category,
        EquipmentType equipment,
        String youtubeUrl,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
