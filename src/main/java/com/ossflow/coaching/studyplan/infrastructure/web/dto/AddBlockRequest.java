package com.ossflow.coaching.studyplan.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

public record AddBlockRequest(
        @Size(max = 200) String title
) {}
