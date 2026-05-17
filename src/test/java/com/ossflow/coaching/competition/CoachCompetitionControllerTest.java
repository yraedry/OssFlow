package com.ossflow.coaching.competition;

import com.ossflow.journal.competitionlog.application.CompetitionLogService;
import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import com.ossflow.journal.competitionlog.infrastructure.web.CompetitionLogWebMapper;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.CompetitionLogResponse;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CoachCompetitionControllerTest {

    @Mock CompetitionLogService service;
    @Mock CompetitionLogWebMapper mapper;
    MockMvc mvc;

    static final long COACH_ID   = 10L;
    static final long ATHLETE_ID = 42L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new CoachCompetitionController(service, mapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private CompetitionLog sampleLog() {
        return CompetitionLog.builder()
                .id(1L).ownerId(ATHLETE_ID)
                .eventName("Copa BJJ Madrid").eventDate(LocalDate.of(2026, 5, 1))
                .build();
    }

    private CompetitionLogResponse sampleResponse() {
        return new CompetitionLogResponse(1L, "Copa BJJ Madrid", LocalDate.of(2026, 5, 1),
                null, null, null, null, null, null, null, List.of(), null, null);
    }

    @Test
    void list_returns_200_with_page() throws Exception {
        var page = new PageImpl<>(List.of(sampleLog()), PageRequest.of(0, 20), 1);
        given(service.listForCoach(eq(COACH_ID), eq(ATHLETE_ID), any())).willReturn(page);
        given(mapper.toResponse(any())).willReturn(sampleResponse());

        mvc.perform(get("/api/v1/coaching/athletes/{athleteId}/competition-logs", ATHLETE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].eventName").value("Copa BJJ Madrid"));
    }

    @Test
    void list_returns_403_when_not_linked() throws Exception {
        given(service.listForCoach(eq(COACH_ID), eq(ATHLETE_ID), any()))
                .willThrow(new ForbiddenException("NOT_YOUR_ATHLETE", "No vinculado"));

        mvc.perform(get("/api/v1/coaching/athletes/{athleteId}/competition-logs", ATHLETE_ID))
                .andExpect(status().isForbidden());
    }
}
