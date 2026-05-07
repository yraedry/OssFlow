package com.ossflow.dashboard.infrastructure.web.dto;

import java.time.LocalDate;

public record WeeklyStatsResponse(
        int weekNumber,
        LocalDate weekStart,
        LocalDate weekEnd,
        long bjjSessions,
        long physicalSessions,
        int bjjGoal,
        int physicalGoal,
        long streakDays,
        long techniquesThisMonth
) {}
