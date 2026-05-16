package com.ossflow.coaching.note.domain;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record AthleteNote(
        Long id,
        Long coachId,
        Long athleteId,
        String body,
        TechniqueFamily techniqueFamily,
        Instant deletedAt,
        Instant readAt,
        Instant createdAt
) {}
