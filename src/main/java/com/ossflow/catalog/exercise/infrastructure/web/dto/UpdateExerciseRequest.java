package com.ossflow.catalog.exercise.infrastructure.web.dto;

import com.ossflow.catalog.exercise.domain.EquipmentType;
import com.ossflow.catalog.exercise.domain.ExerciseCategory;
import com.ossflow.catalog.position.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateExerciseRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 10000) String description,
        @NotNull ExerciseCategory category,
        @NotNull EquipmentType equipment,
        @Pattern(regexp = "^(https?://.*)?$") @Size(max = 500) String youtubeUrl,
        Visibility visibility
) {}
