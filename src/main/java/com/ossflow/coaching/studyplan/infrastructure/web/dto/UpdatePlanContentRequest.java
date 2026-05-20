package com.ossflow.coaching.studyplan.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePlanContentRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 5000) String description
) {}
