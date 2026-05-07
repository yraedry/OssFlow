package com.ossflow.dashboard;

import com.ossflow.dashboard.infrastructure.web.WeeklyStatsController;
import com.ossflow.journal.physicalsession.application.PhysicalSessionService;
import com.ossflow.journal.trainingsession.application.TrainingSessionService;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.shared.web.CurrentOwner;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WeeklyStatsControllerTest {

    private final TrainingSessionService trainingService = mock(TrainingSessionService.class);
    private final PhysicalSessionService physicalService = mock(PhysicalSessionService.class);
    private final CurrentOwner currentOwner = mock(CurrentOwner.class);
    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new WeeklyStatsController(trainingService, physicalService, currentOwner))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void weekly_stats_returns_200_with_goals() throws Exception {
        when(currentOwner.id()).thenReturn(1L);
        when(trainingService.countByWeek(eq(1L), any(LocalDate.class), any(LocalDate.class))).thenReturn(3L);
        when(physicalService.countByWeek(eq(1L), any(LocalDate.class), any(LocalDate.class))).thenReturn(2L);

        mvc.perform(get("/api/v1/dashboard/weekly-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bjjSessions").value(3))
                .andExpect(jsonPath("$.physicalSessions").value(2))
                .andExpect(jsonPath("$.bjjGoal").value(4))
                .andExpect(jsonPath("$.physicalGoal").value(3));
    }
}
