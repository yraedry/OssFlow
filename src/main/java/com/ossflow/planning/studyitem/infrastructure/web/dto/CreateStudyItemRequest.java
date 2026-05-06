package com.ossflow.planning.studyitem.infrastructure.web.dto;

import com.ossflow.planning.studyitem.domain.StudyItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateStudyItemRequest(
        @NotBlank @Size(max = 2000) String description,
        StudyItemStatus status,
        String targetType,
        Long targetId,
        LocalDate dueDate,
        Boolean aiGenerated
) {}
