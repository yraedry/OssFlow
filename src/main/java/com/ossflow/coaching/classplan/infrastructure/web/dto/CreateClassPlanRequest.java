package com.ossflow.coaching.classplan.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateClassPlanRequest(
        @NotNull Long gymId,
        @NotBlank @Size(max = 200) String title,
        String description,
        LocalDate scheduledDate,
        Integer durationMinutes,
        String modality
) {}
