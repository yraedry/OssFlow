package com.ossflow.planning.studyblock.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record StudyBlock(
        Long id,
        Long studyPlanId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        int blockOrder,
        String notesMarkdown,
        String focusEntities,
        Instant createdAt,
        Instant updatedAt
) {}
