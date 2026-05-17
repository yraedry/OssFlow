package com.ossflow.coaching.studyplan.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.coaching.studyplan.application.CoachStudyPlanService;
import com.ossflow.coaching.studyplan.domain.CoachStudyPlan;
import com.ossflow.coaching.studyplan.domain.StudyPlanStatus;
import com.ossflow.coaching.studyplan.infrastructure.web.dto.DuplicatePlanRequest;
import com.ossflow.coaching.studyplan.infrastructure.web.dto.UpdateBlockTitleRequest;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CoachStudyPlanControllerTest {

    @Mock CoachStudyPlanService service;
    MockMvc mvc;
    ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    static final long COACH_ID   = 10L;
    static final long PLAN_ID    = 1L;
    static final long BLOCK_ID   = 2L;
    static final long ATHLETE_ID = 42L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new CoachStudyPlanController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    @Test
    void updateBlockTitle_returns_204() throws Exception {
        doNothing().when(service).updateBlockTitle(eq(PLAN_ID), eq(BLOCK_ID), eq(COACH_ID), any());

        mvc.perform(patch("/api/v1/coaching/study-plans/{planId}/blocks/{blockId}", PLAN_ID, BLOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new UpdateBlockTitleRequest("Calentamiento"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateBlockTitle_unknownPlan_returns_403() throws Exception {
        doThrow(new ForbiddenException("PLAN_ACCESS_DENIED", "Not your plan"))
                .when(service).updateBlockTitle(eq(PLAN_ID), eq(BLOCK_ID), eq(COACH_ID), any());

        mvc.perform(patch("/api/v1/coaching/study-plans/{planId}/blocks/{blockId}", PLAN_ID, BLOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new UpdateBlockTitleRequest("X"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void duplicatePlan_returns_201_with_copia_suffix() throws Exception {
        var copy = CoachStudyPlan.builder()
                .id(99L)
                .coachId(COACH_ID)
                .athleteId(ATHLETE_ID)
                .title("Plan Base (copia)")
                .status(StudyPlanStatus.DRAFT)
                .viewedByAthlete(false)
                .blocks(List.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        given(service.duplicatePlan(eq(PLAN_ID), eq(COACH_ID), eq(ATHLETE_ID))).willReturn(copy);

        mvc.perform(post("/api/v1/coaching/study-plans/{planId}/duplicate", PLAN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new DuplicatePlanRequest(ATHLETE_ID))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.title").value("Plan Base (copia)"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void duplicatePlan_notYourPlan_returns_403() throws Exception {
        given(service.duplicatePlan(eq(PLAN_ID), eq(COACH_ID), eq(ATHLETE_ID)))
                .willThrow(new ForbiddenException("PLAN_ACCESS_DENIED", "Not your plan"));

        mvc.perform(post("/api/v1/coaching/study-plans/{planId}/duplicate", PLAN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new DuplicatePlanRequest(ATHLETE_ID))))
                .andExpect(status().isForbidden());
    }
}
