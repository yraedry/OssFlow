package com.ossflow.coaching.recommendation.infrastructure.web.dto;

import java.time.Instant;

public record RecommendationResponse(
        Long id,
        Long techniqueId,
        String techniqueName,
        String techniqueFamily,
        String note,
        String status,
        String coachName,
        Instant recommendedAt,
        Instant resolvedAt
) {}
