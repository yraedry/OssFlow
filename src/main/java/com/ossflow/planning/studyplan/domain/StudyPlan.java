package com.ossflow.planning.studyplan.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record StudyPlan(
        Long id,
        Long ownerId,
        String title,
        String goalMarkdown,
        LocalDate startDate,
        LocalDate endDate,
        StudyPlanStatus status,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
