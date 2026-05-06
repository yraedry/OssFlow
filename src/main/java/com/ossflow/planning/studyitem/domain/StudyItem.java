package com.ossflow.planning.studyitem.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record StudyItem(
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
