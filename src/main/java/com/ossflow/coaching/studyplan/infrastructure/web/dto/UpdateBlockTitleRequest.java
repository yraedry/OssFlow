package com.ossflow.coaching.studyplan.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateBlockTitleRequest(@NotNull String title) {}
