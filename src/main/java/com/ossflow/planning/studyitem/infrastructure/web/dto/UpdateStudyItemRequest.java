package com.ossflow.planning.studyitem.infrastructure.web.dto;

import com.ossflow.planning.studyitem.domain.StudyItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateStudyItemRequest(
        @NotBlank @Size(max = 2000) String description,
        StudyItemStatus status,
        @Size(max = 50) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String targetType,
        Long targetId,
        LocalDate dueDate,
        boolean aiGenerated
) {}
