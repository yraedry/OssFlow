package com.ossflow.coaching.privatesession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ossflow.coaching.privatesession.application.PrivateSessionService;
import com.ossflow.coaching.privatesession.domain.PrivateSession;
import com.ossflow.coaching.privatesession.infrastructure.web.PrivateSessionController;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PrivateSessionControllerTest {

    @Mock PrivateSessionService service;
    MockMvc mvc;
    ObjectMapper json = new ObjectMapper().registerModule(new JavaTimeModule());

    static final long COACH_ID = 10L;
    static final long ATHLETE_ID = 42L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new PrivateSessionController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private PrivateSession sample() {
        return PrivateSession.builder()
                .id(1L).coachId(COACH_ID).athleteId(ATHLETE_ID)
                .sessionDate(LocalDate.of(2026, 5, 17))
                .title("Single leg X")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void create_returns_201() throws Exception {
        given(service.create(eq(COACH_ID), any())).willReturn(sample());

        mvc.perform(post("/api/v1/coaching/private-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "athleteId", ATHLETE_ID,
                                "sessionDate", "2026-05-17",
                                "title", "Single leg X"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Single leg X"));
    }

    @Test
    void create_returns_403_when_not_linked() throws Exception {
        given(service.create(eq(COACH_ID), any()))
                .willThrow(new ForbiddenException("SESSION_NOT_YOUR_ATHLETE", "Not your athlete"));

        mvc.perform(post("/api/v1/coaching/private-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "athleteId", ATHLETE_ID,
                                "sessionDate", "2026-05-17"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listByAthlete_returns_200() throws Exception {
        given(service.listByAthlete(COACH_ID, ATHLETE_ID)).willReturn(List.of(sample()));

        mvc.perform(get("/api/v1/coaching/private-sessions")
                        .param("athleteId", String.valueOf(ATHLETE_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void listAll_returns_200() throws Exception {
        given(service.listAll(COACH_ID)).willReturn(List.of(sample()));

        mvc.perform(get("/api/v1/coaching/private-sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void update_returns_200() throws Exception {
        given(service.update(eq(COACH_ID), eq(1L), any())).willReturn(sample());

        mvc.perform(put("/api/v1/coaching/private-sessions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("title", "Updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_returns_204() throws Exception {
        doNothing().when(service).delete(COACH_ID, 1L);

        mvc.perform(delete("/api/v1/coaching/private-sessions/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void mine_returns_200() throws Exception {
        given(service.listMine(COACH_ID)).willReturn(List.of(sample()));

        mvc.perform(get("/api/v1/coaching/private-sessions/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
