package com.ossflow.catalog.ruleset.infrastructure.web.dto;

import com.ossflow.catalog.ruleset.domain.LegalityStatus;

public record RulesetTechniqueResponse(
        Long techniqueId,
        LegalityStatus status,
        String conditionNotes
) {}
