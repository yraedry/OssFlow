package com.ossflow.planning.studyplan.infrastructure.web.dto;

import com.ossflow.planning.studyplan.domain.StudyPlanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateStudyPlanRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 10000) String goalMarkdown,
        LocalDate startDate,
        LocalDate endDate,
        @NotNull StudyPlanStatus status
) {}
