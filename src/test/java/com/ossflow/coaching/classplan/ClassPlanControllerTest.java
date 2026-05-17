package com.ossflow.coaching.classplan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ossflow.coaching.classplan.application.ClassPlanService;
import com.ossflow.coaching.classplan.domain.ClassPlan;
import com.ossflow.coaching.classplan.domain.ClassPlanStatus;
import com.ossflow.coaching.classplan.infrastructure.web.ClassPlanController;
import com.ossflow.coaching.classplan.infrastructure.web.dto.CreateClassPlanRequest;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClassPlanControllerTest {

    @Mock ClassPlanService service;
    MockMvc mvc;
    ObjectMapper json = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    static final long COACH_ID = 10L;
    static final long GYM_ID   = 1L;
    static final long PLAN_ID  = 5L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new ClassPlanController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    ClassPlan plan() {
        return ClassPlan.builder()
                .id(PLAN_ID).coachId(COACH_ID).gymId(GYM_ID)
                .title("Clase lunes").status(ClassPlanStatus.DRAFT)
                .blocks(List.of()).createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    @Test
    void create_returns201() throws Exception {
        given(service.create(eq(COACH_ID), eq(GYM_ID), eq("Clase lunes"), any(), any(), any(), any()))
                .willReturn(plan());

        mvc.perform(post("/api/v1/coaching/class-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                new CreateClassPlanRequest(GYM_ID, "Clase lunes", null, null, null, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PLAN_ID));
    }

    @Test
    void list_returns200() throws Exception {
        given(service.list(COACH_ID, GYM_ID)).willReturn(List.of(plan()));

        mvc.perform(get("/api/v1/coaching/class-plans").param("gymId", String.valueOf(GYM_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gymId").value(GYM_ID));
    }

    @Test
    void get_returns200() throws Exception {
        given(service.get(PLAN_ID, COACH_ID)).willReturn(plan());

        mvc.perform(get("/api/v1/coaching/class-plans/{id}", PLAN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clase lunes"));
    }

    @Test
    void update_returns200() throws Exception {
        given(service.update(eq(PLAN_ID), eq(COACH_ID), any(), any(), any(), any(), any(), any()))
                .willReturn(plan());

        mvc.perform(put("/api/v1/coaching/class-plans/{id}", PLAN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Clase lunes\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns204() throws Exception {
        mvc.perform(delete("/api/v1/coaching/class-plans/{id}", PLAN_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void create_gymNotOwned_returns403() throws Exception {
        given(service.create(eq(COACH_ID), eq(GYM_ID), any(), any(), any(), any(), any()))
                .willThrow(new ForbiddenException("GYM_ACCESS_DENIED", "Not your gym"));

        mvc.perform(post("/api/v1/coaching/class-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                new CreateClassPlanRequest(GYM_ID, "X", null, null, null, null))))
                .andExpect(status().isForbidden());
    }
}
