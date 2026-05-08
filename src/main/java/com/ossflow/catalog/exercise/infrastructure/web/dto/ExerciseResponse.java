package com.ossflow.catalog.exercise.infrastructure.web.dto;

import com.ossflow.catalog.exercise.domain.EquipmentType;
import com.ossflow.catalog.exercise.domain.ExerciseCategory;
import com.ossflow.catalog.position.domain.Visibility;

import java.time.Instant;

public record ExerciseResponse(
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
        Long version
) {}
