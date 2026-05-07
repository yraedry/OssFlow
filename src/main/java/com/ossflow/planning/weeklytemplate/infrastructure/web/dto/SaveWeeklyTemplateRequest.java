package com.ossflow.planning.weeklytemplate.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SaveWeeklyTemplateRequest(
        @NotNull @Size(max = 7) @Valid List<DayEntryDto> days
) {}
