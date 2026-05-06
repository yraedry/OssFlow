package com.ossflow.catalog.ruleset.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record RulesetTechnique(
        Long rulesetId,
        Long techniqueId,
        LegalityStatus status,
        String conditionNotes
) {}
