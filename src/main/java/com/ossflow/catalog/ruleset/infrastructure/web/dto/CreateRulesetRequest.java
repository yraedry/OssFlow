package com.ossflow.catalog.ruleset.infrastructure.web.dto;

import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateRulesetRequest(
        @NotNull Long federationId,
        @NotNull Belt belt,
        @NotNull Modality modality,
        @NotNull LocalDate effectiveFrom,
        LocalDate effectiveTo,
        String sourceUrl
) {}
