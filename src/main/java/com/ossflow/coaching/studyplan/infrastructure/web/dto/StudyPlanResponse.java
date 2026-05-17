package com.ossflow.coaching.studyplan.infrastructure.web.dto;

import java.time.Instant;
import java.util.List;

public record StudyPlanResponse(
        Long id,
        Long coachId,
        Long athleteId,
        String title,
        String description,
        String status,
        boolean viewedByAthlete,
        List<StudyBlockResponse> blocks,
        Instant createdAt,
        Instant updatedAt
) {}
