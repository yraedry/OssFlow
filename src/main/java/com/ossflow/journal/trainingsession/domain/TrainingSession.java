package com.ossflow.journal.trainingsession.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record TrainingSession(
        Long id,
        Long ownerId,
        LocalDate sessionDate,
        int durationMinutes,
        String location,
        Intensity intensity,
        SessionType sessionType,
        String notesMarkdown,
        List<WorkedTechnique> workedTechniques,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
