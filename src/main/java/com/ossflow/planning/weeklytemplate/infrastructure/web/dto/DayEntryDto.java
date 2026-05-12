package com.ossflow.planning.weeklytemplate.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.List;

public record DayEntryDto(
        @NotNull DayOfWeek dayOfWeek,
        @NotNull @Valid List<SessionSlotDto> sessions
) {}
