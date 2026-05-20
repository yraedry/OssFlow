package com.ossflow.coaching.studyplan.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record AddTechniqueItemRequest(
        @NotNull Long techniqueId
) {}
