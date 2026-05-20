package com.ossflow.coaching.recommendation.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateRecommendationRequest(
        @NotNull @Positive Long techniqueId,
        @NotNull Long athleteId,
        @Size(max = 1000) String note
) {}
