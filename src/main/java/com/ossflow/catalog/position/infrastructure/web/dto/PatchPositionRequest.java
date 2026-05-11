package com.ossflow.catalog.position.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatchPositionRequest(
        @Size(max = 120) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String name,
        PositionType type,
        @Size(max = 10000) String description,
        @Size(max = 500) String youtubeUrl,
        Visibility visibility
) {}
