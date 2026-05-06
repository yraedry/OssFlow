package com.ossflow.catalog.ruleset.infrastructure.web.dto;

import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record RulesetResponse(
        Long id,
        Long federationId,
        Belt belt,
        Modality modality,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        String sourceUrl,
        List<RulesetTechniqueResponse> techniques,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
