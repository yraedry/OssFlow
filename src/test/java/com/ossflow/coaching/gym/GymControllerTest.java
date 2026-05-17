package com.ossflow.coaching.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.coaching.gym.application.GymService;
import com.ossflow.coaching.gym.domain.GymLocation;
import com.ossflow.coaching.gym.infrastructure.web.GymController;
import com.ossflow.coaching.gym.infrastructure.web.dto.CreateGymRequest;
import com.ossflow.coaching.gym.infrastructure.web.dto.UpdateGymRequest;
import com.ossflow.shared.exception.ConflictException;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GymControllerTest {
    @Mock GymService service;
    MockMvc mvc;
    ObjectMapper json = new ObjectMapper().findAndRegisterModules();
    static final long COACH_ID = 10L;
    static final long GYM_ID = 1L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new GymController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    GymLocation sample() {
        return new GymLocation(GYM_ID, COACH_ID, "Academia Central", null, Instant.now());
    }

    @Test
    void create_returns201() throws Exception {
        given(service.create(eq(COACH_ID), eq("Academia Central"), isNull())).willReturn(sample());
        mvc.perform(post("/api/v1/coaching/gyms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new CreateGymRequest("Academia Central", null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(GYM_ID))
                .andExpect(jsonPath("$.name").value("Academia Central"));
    }

    @Test
    void list_returns200() throws Exception {
        given(service.list(COACH_ID)).willReturn(List.of(sample()));
        mvc.perform(get("/api/v1/coaching/gyms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(GYM_ID));
    }

    @Test
    void update_returns200() throws Exception {
        given(service.update(eq(GYM_ID), eq(COACH_ID), eq("Nuevo nombre"), isNull()))
                .willReturn(new GymLocation(GYM_ID, COACH_ID, "Nuevo nombre", null, Instant.now()));
        mvc.perform(put("/api/v1/coaching/gyms/{id}", GYM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new UpdateGymRequest("Nuevo nombre", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nuevo nombre"));
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(service).delete(GYM_ID, COACH_ID);
        mvc.perform(delete("/api/v1/coaching/gyms/{id}", GYM_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_withPlans_returns409() throws Exception {
        doThrow(new ConflictException("GYM_HAS_PLANS", "Cannot delete gym with existing class plans"))
                .when(service).delete(GYM_ID, COACH_ID);
        mvc.perform(delete("/api/v1/coaching/gyms/{id}", GYM_ID))
                .andExpect(status().isConflict());
    }
}
