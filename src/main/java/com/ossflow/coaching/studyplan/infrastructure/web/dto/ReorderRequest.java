package com.ossflow.coaching.studyplan.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReorderRequest(
        @NotNull List<Long> orderedIds
) {}
