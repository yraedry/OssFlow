package com.ossflow.coaching.classplan.infrastructure.web.dto;

import com.ossflow.coaching.classplan.domain.ClassPlanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateClassPlanRequest(
        @NotBlank @Size(max = 200) String title,
        String description,
        LocalDate scheduledDate,
        Integer durationMinutes,
        String modality,
        ClassPlanStatus status
) {}
