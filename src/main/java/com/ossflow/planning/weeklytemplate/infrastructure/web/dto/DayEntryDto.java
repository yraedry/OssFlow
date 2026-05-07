package com.ossflow.planning.weeklytemplate.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;

public record DayEntryDto(
        @NotNull DayOfWeek dayOfWeek,
        boolean bjj,
        boolean strength,
        boolean cardio
) {}
