package com.ossflow.coaching.recommendation.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.coaching.recommendation.application.RecommendationService;
import com.ossflow.coaching.recommendation.domain.RecommendationStatus;
import com.ossflow.coaching.recommendation.domain.TechniqueRecommendation;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.shared.exception.NotFoundException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock RecommendationService service;
    @Mock TechniqueRepositoryPort techniqueRepo;
    MockMvc mvc;
    ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    static final long COACH_ID = 10L;
    static final long ATHLETE_ID = 42L;
    static final long TECHNIQUE_ID = 5L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new RecommendationController(service, techniqueRepo))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private TechniqueRecommendation sampleRec() {
        return TechniqueRecommendation.builder()
                .id(1L)
                .coachId(COACH_ID)
                .athleteId(ATHLETE_ID)
                .techniqueId(TECHNIQUE_ID)
                .status(RecommendationStatus.PENDING)
                .recommendedAt(Instant.now())
                .build();
    }

    private Technique sampleTechnique() {
        return Technique.builder()
                .id(TECHNIQUE_ID)
                .name("Armbar")
                .build();
    }

    @Test
    void create_happyPath_returns201() throws Exception {
        given(service.create(eq(COACH_ID), any())).willReturn(sampleRec());
        given(techniqueRepo.findById(TECHNIQUE_ID, COACH_ID)).willReturn(Optional.of(sampleTechnique()));

        mvc.perform(post("/api/v1/coaching/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "techniqueId", TECHNIQUE_ID,
                                "athleteId", ATHLETE_ID,
                                "note", "Practica esto"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.techniqueName").value("Armbar"));
    }

    @Test
    void create_invalidPair_returns403() throws Exception {
        given(service.create(eq(COACH_ID), any()))
                .willThrow(new ForbiddenException("RECOMMENDATION_NOT_YOUR_ATHLETE", "Not your athlete"));

        mvc.perform(post("/api/v1/coaching/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "techniqueId", TECHNIQUE_ID,
                                "athleteId", ATHLETE_ID))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listSent_returns200() throws Exception {
        given(service.listSent(COACH_ID, ATHLETE_ID)).willReturn(List.of(sampleRec()));
        given(techniqueRepo.findById(TECHNIQUE_ID, COACH_ID)).willReturn(Optional.of(sampleTechnique()));

        mvc.perform(get("/api/v1/coaching/recommendations/sent/athlete/{athleteId}", ATHLETE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].techniqueName").value("Armbar"));
    }

    @Test
    void cancel_returns204() throws Exception {
        doNothing().when(service).cancel(COACH_ID, 1L);

        mvc.perform(patch("/api/v1/coaching/recommendations/1/cancel"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listReceived_returns200() throws Exception {
        given(service.listReceived(COACH_ID)).willReturn(List.of(sampleRec()));
        given(techniqueRepo.findById(TECHNIQUE_ID, COACH_ID)).willReturn(Optional.of(sampleTechnique()));

        mvc.perform(get("/api/v1/coaching/recommendations/received"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void accept_returns204() throws Exception {
        doNothing().when(service).accept(COACH_ID, 1L);

        mvc.perform(patch("/api/v1/coaching/recommendations/1/accept"))
                .andExpect(status().isNoContent());
    }

    @Test
    void dismiss_returns204() throws Exception {
        doNothing().when(service).dismiss(COACH_ID, 1L);

        mvc.perform(patch("/api/v1/coaching/recommendations/1/dismiss"))
                .andExpect(status().isNoContent());
    }
}
