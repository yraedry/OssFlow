package com.ossflow.planning.studyitem.infrastructure.web.dto;

import com.ossflow.planning.studyitem.domain.StudyItemStatus;

import java.time.Instant;
import java.time.LocalDate;

public record StudyItemResponse(
        Long id,
        Long studyBlockId,
        String description,
        StudyItemStatus status,
        String targetType,
        Long targetId,
        LocalDate dueDate,
        boolean aiGenerated,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
