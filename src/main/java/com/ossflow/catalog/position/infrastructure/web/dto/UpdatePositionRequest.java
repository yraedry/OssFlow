package com.ossflow.catalog.position.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePositionRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull PositionType type,
        @Size(max = 10000) String description,
        @Size(max = 500) String youtubeUrl,
        @NotNull Visibility visibility
) {}
