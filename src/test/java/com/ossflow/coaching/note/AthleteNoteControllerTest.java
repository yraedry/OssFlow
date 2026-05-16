package com.ossflow.coaching.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.coaching.note.application.AthleteNoteService;
import com.ossflow.coaching.note.domain.AthleteNote;
import com.ossflow.coaching.note.infrastructure.web.AthleteNoteController;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AthleteNoteControllerTest {

    @Mock AthleteNoteService service;
    MockMvc mvc;
    ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    static final long COACH_ID = 10L;
    static final long ATHLETE_ID = 42L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new AthleteNoteController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private AthleteNote sampleNote() {
        return AthleteNote.builder()
                .id(1L)
                .coachId(COACH_ID)
                .athleteId(ATHLETE_ID)
                .body("Trabajo de guardia excelente")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void create_returns_201() throws Exception {
        given(service.create(eq(COACH_ID), any())).willReturn(sampleNote());

        mvc.perform(post("/api/v1/coaching/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "athleteId", ATHLETE_ID,
                                "body", "Trabajo de guardia excelente"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.body").value("Trabajo de guardia excelente"));
    }

    @Test
    void create_returns_403_when_not_linked() throws Exception {
        given(service.create(eq(COACH_ID), any()))
                .willThrow(new ForbiddenException("NOTE_NOT_YOUR_ATHLETE", "Not your athlete"));

        mvc.perform(post("/api/v1/coaching/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "athleteId", ATHLETE_ID,
                                "body", "some note"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_returns_204() throws Exception {
        doNothing().when(service).softDelete(COACH_ID, 1L);

        mvc.perform(delete("/api/v1/coaching/notes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listReceived_returns_200() throws Exception {
        given(service.listReceived(COACH_ID)).willReturn(List.of(sampleNote()));

        mvc.perform(get("/api/v1/coaching/notes/received"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void markRead_returns_204() throws Exception {
        doNothing().when(service).markRead(COACH_ID, 1L);

        mvc.perform(patch("/api/v1/coaching/notes/received/1/read"))
                .andExpect(status().isNoContent());
    }

    @Test
    void countUnread_returns_200() throws Exception {
        given(service.countUnread(COACH_ID)).willReturn(3L);

        mvc.perform(get("/api/v1/coaching/notes/received/unread-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void getReceivedDetail_returns_200() throws Exception {
        given(service.getReceivedDetail(COACH_ID, 1L)).willReturn(sampleNote());

        mvc.perform(get("/api/v1/coaching/notes/received/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.body").value("Trabajo de guardia excelente"));
    }
}
