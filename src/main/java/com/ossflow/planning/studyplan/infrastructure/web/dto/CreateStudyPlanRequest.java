package com.ossflow.planning.studyplan.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ossflow.planning.studyplan.domain.StudyPlanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateStudyPlanRequest(
        @NotBlank @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String title,
        @Size(max = 10000) String goalMarkdown,
        LocalDate startDate,
        LocalDate endDate,
        // Si el frontend no manda status, default DRAFT.
        @JsonSetter(nulls = Nulls.SKIP) StudyPlanStatus status
) {
    public CreateStudyPlanRequest {
        if (status == null) status = StudyPlanStatus.DRAFT;
    }
}
