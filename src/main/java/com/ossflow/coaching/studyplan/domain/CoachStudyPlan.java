package com.ossflow.coaching.studyplan.domain;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record CoachStudyPlan(
        Long id,
        Long coachId,
        Long athleteId,
        String title,
        String description,
        StudyPlanStatus status,
        boolean viewedByAthlete,
        List<CoachStudyBlock> blocks,
        Instant createdAt,
        Instant updatedAt
) {}
