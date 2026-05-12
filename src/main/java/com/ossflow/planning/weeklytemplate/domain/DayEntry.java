package com.ossflow.planning.weeklytemplate.domain;

import lombok.Builder;
import java.time.DayOfWeek;

@Builder(toBuilder = true)
public record DayEntry(
        DayOfWeek dayOfWeek,
        boolean bjj,
        boolean strength,
        boolean cardio,
        boolean mobility,
        boolean flexibility
) {}
