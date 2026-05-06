package com.ossflow.catalog.technique.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTechniqueRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull TechniqueCategory category,
        @Size(max = 10000) String description,
        @Pattern(regexp = "^https?://.*") String youtubeUrl,
        @NotNull Belt minimumBelt,
        @NotNull Modality modality,
        @NotNull Long startPositionId,
        Long endPositionId,
        @NotNull Visibility visibility
) {}
