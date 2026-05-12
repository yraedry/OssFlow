package com.ossflow.planning.weeklytemplate.domain;

import lombok.Builder;
import java.time.DayOfWeek;
import java.util.List;

@Builder(toBuilder = true)
public record DayEntry(
        DayOfWeek dayOfWeek,
        List<SessionSlot> sessions
) {}
