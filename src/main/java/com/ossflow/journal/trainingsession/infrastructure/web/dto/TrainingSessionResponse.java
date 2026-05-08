package com.ossflow.journal.trainingsession.infrastructure.web.dto;

import com.ossflow.journal.trainingsession.domain.Intensity;
import com.ossflow.journal.trainingsession.domain.SessionType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record TrainingSessionResponse(
        Long id,
        LocalDate sessionDate,
        int durationMinutes,
        String location,
        Intensity intensity,
        SessionType sessionType,
        String notesMarkdown,
        List<WorkedTechniqueResponse> workedTechniques,
        Instant createdAt,
        Instant updatedAt
) {}
