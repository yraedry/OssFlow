package com.ossflow.coaching.recommendation.domain;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record TechniqueRecommendation(
        Long id,
        Long coachId,
        Long athleteId,
        Long techniqueId,
        String note,
        RecommendationStatus status,
        Instant recommendedAt,
        Instant resolvedAt
) {}
