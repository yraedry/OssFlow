package com.ossflow.coaching.observation.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.technique.domain.TechniqueFamily;
import com.ossflow.coaching.observation.application.CoachObservationService;
import com.ossflow.coaching.observation.application.RadarRow;
import com.ossflow.coaching.observation.domain.CoachObservation;
import com.ossflow.coaching.observation.domain.Tone;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CoachObservationControllerTest {

    @Mock CoachObservationService service;
    MockMvc mvc;
    ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    static final long COACH_ID = 10L;
    static final long ATHLETE_ID = 42L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new CoachObservationController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private CoachObservation sample() {
        return CoachObservation.builder()
                .id(1L).coachId(COACH_ID).athleteId(ATHLETE_ID)
                .body("buen X-pass").tone(Tone.POSITIVE)
                .techniqueFamily(TechniqueFamily.GUARD_PASSES)
                .observedAt(Instant.now()).createdAt(Instant.now()).build();
    }

    @Test
    void create_returns_201() throws Exception {
        given(service.create(eq(COACH_ID), any())).willReturn(sample());
        mvc.perform(post("/api/v1/coaching/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "athleteId", ATHLETE_ID, "body", "buen X-pass",
                                "tone", "POSITIVE", "techniqueFamily", "GUARD_PASSES"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_returns_403_when_not_linked() throws Exception {
        given(service.create(eq(COACH_ID), any()))
                .willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your athlete"));
        mvc.perform(post("/api/v1/coaching/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "athleteId", ATHLETE_ID, "body", "test", "tone", "NEUTRAL"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_returns_200() throws Exception {
        given(service.list(COACH_ID, ATHLETE_ID)).willReturn(List.of(sample()));
        mvc.perform(get("/api/v1/coaching/observations/athlete/" + ATHLETE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body").value("buen X-pass"));
    }

    @Test
    void list_returns_403_when_not_linked() throws Exception {
        given(service.list(COACH_ID, ATHLETE_ID))
                .willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your athlete"));
        mvc.perform(get("/api/v1/coaching/observations/athlete/" + ATHLETE_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_returns_204() throws Exception {
        doNothing().when(service).delete(COACH_ID, 1L);
        mvc.perform(delete("/api/v1/coaching/observations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns_404_when_not_found() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"))
                .when(service).delete(COACH_ID, 99L);
        mvc.perform(delete("/api/v1/coaching/observations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void radar_returns_200() throws Exception {
        given(service.radar(COACH_ID, ATHLETE_ID))
                .willReturn(List.of(new RadarRow(TechniqueFamily.CHOKES, 3L)));
        mvc.perform(get("/api/v1/coaching/observations/athlete/" + ATHLETE_ID + "/radar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].family").value("CHOKES"))
                .andExpect(jsonPath("$[0].score").value(3));
    }

    @Test
    void radar_returns_403_when_not_linked() throws Exception {
        given(service.radar(COACH_ID, ATHLETE_ID))
                .willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your athlete"));
        mvc.perform(get("/api/v1/coaching/observations/athlete/" + ATHLETE_ID + "/radar"))
                .andExpect(status().isForbidden());
    }
}
