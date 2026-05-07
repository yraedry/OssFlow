package com.ossflow.planning.weeklytemplate.infrastructure.web.dto;

import java.time.Instant;
import java.util.List;

public record WeeklyTemplateResponse(
        Long id,
        List<DayEntryDto> days,
        Instant updatedAt
) {}
