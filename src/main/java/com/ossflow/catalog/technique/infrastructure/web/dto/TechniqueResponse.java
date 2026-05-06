package com.ossflow.catalog.technique.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.TechniqueCategory;

import java.time.Instant;

public record TechniqueResponse(
        Long id,
        String name,
        TechniqueCategory category,
        String description,
        String youtubeUrl,
        Belt minimumBelt,
        Modality modality,
        Long startPositionId,
        Long endPositionId,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
