package com.ossflow.planning.studyplan.infrastructure.web.dto;

import com.ossflow.planning.studyplan.domain.StudyPlanStatus;

import java.time.Instant;
import java.time.LocalDate;

public record StudyPlanResponse(
        Long id,
        String title,
        String goalMarkdown,
        LocalDate startDate,
        LocalDate endDate,
        StudyPlanStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
