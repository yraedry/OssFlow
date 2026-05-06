package com.ossflow.catalog.ruleset.infrastructure.web.dto;

import com.ossflow.catalog.ruleset.domain.LegalityStatus;
import jakarta.validation.constraints.NotNull;

public record UpsertRulesetTechniqueRequest(
        @NotNull Long techniqueId,
        @NotNull LegalityStatus status,
        String conditionNotes
) {}
