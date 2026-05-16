package com.ossflow.coaching.observation.infrastructure.web.dto;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import com.ossflow.coaching.observation.domain.Tone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateObservationRequest(
        @NotNull Long athleteId,
        @NotBlank @Size(max = 2000) String body,
        @NotNull Tone tone,
        TechniqueFamily techniqueFamily
) {}
