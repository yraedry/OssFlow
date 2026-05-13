package com.ossflow.catalog.ruleset.domain;

import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record Ruleset(
        Long id,
        Long federationId,
        String federationName,
        Belt belt,
        Modality modality,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        String sourceUrl,
        List<RulesetTechnique> techniques,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
