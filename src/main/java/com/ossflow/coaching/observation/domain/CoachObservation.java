package com.ossflow.coaching.observation.domain;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record CoachObservation(
        Long id,
        Long coachId,
        Long athleteId,
        String body,
        Tone tone,
        TechniqueFamily techniqueFamily,
        LabelledBy labelledBy,
        Instant observedAt,
        Instant createdAt
) {}
