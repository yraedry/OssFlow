package com.ossflow.planning.studyblock.infrastructure.web.dto;

import java.time.Instant;
import java.time.LocalDate;

public record StudyBlockResponse(
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
