package com.ossflow.coaching.observation.infrastructure.web.dto;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import com.ossflow.coaching.observation.domain.CoachObservation;
import com.ossflow.coaching.observation.domain.LabelledBy;
import com.ossflow.coaching.observation.domain.Tone;

import java.time.Instant;

public record ObservationResponse(
        Long id,
        Long athleteId,
        String body,
        Tone tone,
        TechniqueFamily techniqueFamily,
        LabelledBy labelledBy,
        Instant observedAt
) {
    public static ObservationResponse from(CoachObservation o) {
        return new ObservationResponse(o.id(), o.athleteId(), o.body(), o.tone(),
                o.techniqueFamily(), o.labelledBy(), o.observedAt());
    }
}
