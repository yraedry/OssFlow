package com.ossflow.coaching.relationship.domain;

import lombok.Builder;
import java.time.Instant;

@Builder(toBuilder = true)
public record CoachAthleteRelationship(
        Long id,
        Long coachId,
        Long athleteId,
        Long invitationId,
        Instant linkedAt
) {}
