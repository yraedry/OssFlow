package com.ossflow.catalog.position.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import jakarta.validation.constraints.Size;

public record PatchPositionRequest(
        @Size(max = 120) String name,
        PositionType type,
        @Size(max = 10000) String description,
        Visibility visibility
) {}
