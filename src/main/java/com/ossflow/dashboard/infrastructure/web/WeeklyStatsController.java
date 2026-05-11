package com.ossflow.dashboard.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.dashboard.infrastructure.web.dto.WeeklyStatsResponse;
import com.ossflow.journal.physicalsession.application.PhysicalSessionService;
import com.ossflow.journal.trainingsession.application.TrainingSessionService;
import com.ossflow.shared.web.CurrentOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class WeeklyStatsController {

    private static final int BJJ_GOAL = 4;
    private static final int PHYSICAL_GOAL = 3;

    private final TrainingSessionService trainingSessionService;
    private final PhysicalSessionService physicalSessionService;
    private final CurrentOwner currentOwner;

    @GetMapping("/weekly-stats")
    public WeeklyStatsResponse weeklyStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        int weekNumber = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        Long ownerId = currentOwner.id();

        long bjjSessions = trainingSessionService.countByWeek(ownerId, weekStart, weekEnd);
        long physicalSessions = physicalSessionService.countByWeek(ownerId, weekStart, weekEnd);

        return new WeeklyStatsResponse(
                weekNumber,
                weekStart,
                weekEnd,
                bjjSessions,
                physicalSessions,
                BJJ_GOAL,
                PHYSICAL_GOAL,
                0L,
                0L
        );
    }
}
