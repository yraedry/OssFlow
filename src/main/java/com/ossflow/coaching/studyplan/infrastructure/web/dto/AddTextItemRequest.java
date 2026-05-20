package com.ossflow.coaching.studyplan.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddTextItemRequest(
        @NotBlank @Size(max = 2000) String content
) {}
