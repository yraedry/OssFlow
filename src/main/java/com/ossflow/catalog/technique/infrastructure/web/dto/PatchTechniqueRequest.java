package com.ossflow.catalog.technique.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import com.ossflow.catalog.technique.domain.TechniqueFamily;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatchTechniqueRequest(
        @Size(max = 120) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String name,
        TechniqueCategory category,
        TechniqueFamily family,
        @Size(max = 10000) String description,
        @Pattern(regexp = "^https?://.*") String youtubeUrl,
        Belt minimumBelt,
        Modality modality,
        Long startPositionId,
        Long endPositionId,
        Visibility visibility
) {}
