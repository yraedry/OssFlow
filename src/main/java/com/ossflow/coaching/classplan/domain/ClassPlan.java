package com.ossflow.coaching.classplan.domain;

import com.ossflow.coaching.studyplan.domain.CoachStudyBlock;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record ClassPlan(
        Long id,
        Long coachId,
        Long gymId,
        String title,
        String description,
        LocalDate scheduledDate,
        Integer durationMinutes,
        String modality,
        ClassPlanStatus status,
        List<CoachStudyBlock> blocks,
        Instant createdAt,
        Instant updatedAt
) {}
