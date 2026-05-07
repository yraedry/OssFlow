package com.ossflow.planning.weeklytemplate.domain;

import lombok.Builder;
import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record WeeklyTemplate(
        Long id,
        Long ownerId,
        List<DayEntry> days,
        Instant createdAt,
        Instant updatedAt
) {}
