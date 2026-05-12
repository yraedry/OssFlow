package com.ossflow.planning.studyplan.infrastructure.web.dto;

import com.ossflow.planning.studyplan.domain.StudyPlanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateStudyPlanRequest(
        @NotBlank @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String title,
        @Size(max = 10000) String goalMarkdown,
        LocalDate startDate,
        LocalDate endDate,
        StudyPlanStatus status
) {}
