package com.ossflow.catalog.system.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSystemRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 10000) String description,
        Long anchorPositionId,
        @NotBlank String flowDefinition,
        Visibility visibility
) {}
