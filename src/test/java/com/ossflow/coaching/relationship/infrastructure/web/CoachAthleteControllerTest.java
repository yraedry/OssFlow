package com.ossflow.coaching.relationship.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.coaching.relationship.application.AthleteProfileComposer;
import com.ossflow.coaching.relationship.application.CoachAthleteService;
import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import com.ossflow.coaching.relationship.infrastructure.web.dto.AthleteSummaryResponse;
import com.ossflow.coaching.relationship.infrastructure.web.dto.RedeemInvitationRequest;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.shared.exception.ConflictException;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.shared.exception.UnprocessableException;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CoachAthleteControllerTest {

    @Mock CoachAthleteService coachAthleteService;
    @Mock AthleteProfileComposer composer;
    @Mock UserProfileRepositoryPort profileRepo;

    MockMvc mvc;
    ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    static final long ATHLETE_ID = 42L;
    static final long COACH_ID = 10L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setOwner(ATHLETE_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new CoachAthleteController(
                        coachAthleteService, composer, profileRepo))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private CoachAthleteRelationship sampleRelationship() {
        return CoachAthleteRelationship.builder()
                .id(1L).coachId(COACH_ID).athleteId(ATHLETE_ID)
                .invitationId(1L).linkedAt(Instant.now()).build();
    }

    // ── redeem ────────────────────────────────────────────────────────────────

    @Test
    void redeem_returns_204_on_valid_code() throws Exception {
        // coachAthleteService.redeemCode() does nothing (success)
        doNothing().when(coachAthleteService).redeemCode("ABC123", ATHLETE_ID);

        mvc.perform(post("/api/v1/coaching/memberships/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new RedeemInvitationRequest("ABC123"))))
                .andExpect(status().isNoContent());

        verify(coachAthleteService).redeemCode("ABC123", ATHLETE_ID);
    }

    @Test
    void redeem_returns_422_when_code_invalid() throws Exception {
        doThrow(new UnprocessableException("INVALID_CODE", "Código de invitación inválido o expirado"))
                .when(coachAthleteService).redeemCode("ZZZZZZ", ATHLETE_ID);

        mvc.perform(post("/api/v1/coaching/memberships/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new RedeemInvitationRequest("ZZZZZZ"))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void redeem_returns_400_when_code_too_short() throws Exception {
        mvc.perform(post("/api/v1/coaching/memberships/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"AB\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redeem_returns_409_when_already_linked() throws Exception {
        doThrow(new ConflictException("ALREADY_LINKED", "El atleta ya está vinculado a este maestro"))
                .when(coachAthleteService).redeemCode("ABC123", ATHLETE_ID);

        mvc.perform(post("/api/v1/coaching/memberships/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new RedeemInvitationRequest("ABC123"))))
                .andExpect(status().isConflict());
    }

    // ── removeAthlete ─────────────────────────────────────────────────────────

    @Test
    void remove_athlete_returns_204() throws Exception {
        TestSecurityContext.setCoach(COACH_ID);

        mvc.perform(delete("/api/v1/coaching/memberships/" + ATHLETE_ID))
                .andExpect(status().isNoContent());

        verify(coachAthleteService).removeAthleteFromCoach(COACH_ID, ATHLETE_ID);
    }

    // ── leaveCoach ────────────────────────────────────────────────────────────

    @Test
    void leave_coach_returns_204() throws Exception {
        mvc.perform(delete("/api/v1/coaching/memberships/leave/" + COACH_ID))
                .andExpect(status().isNoContent());

        verify(coachAthleteService).leaveCoach(ATHLETE_ID, COACH_ID);
    }

    // ── getAthletes ───────────────────────────────────────────────────────────

    @Test
    void get_athletes_returns_200_list() throws Exception {
        TestSecurityContext.setCoach(COACH_ID);
        given(coachAthleteService.getAthletes(COACH_ID)).willReturn(List.of(sampleRelationship()));
        given(profileRepo.findByOwnerId(ATHLETE_ID)).willReturn(Optional.empty());

        mvc.perform(get("/api/v1/coaching/athletes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].athleteId").value(ATHLETE_ID));
    }

    // ── getAthleteSummary ─────────────────────────────────────────────────────

    @Test
    void get_athlete_summary_returns_200() throws Exception {
        TestSecurityContext.setCoach(COACH_ID);
        var summary = new AthleteSummaryResponse(
                ATHLETE_ID, "John Doe", "blue", 180, "Academy A",
                List.of(), List.of(), null, -1L);
        given(composer.compose(COACH_ID, ATHLETE_ID)).willReturn(summary);

        mvc.perform(get("/api/v1/coaching/athletes/" + ATHLETE_ID + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteId").value(ATHLETE_ID))
                .andExpect(jsonPath("$.displayName").value("John Doe"));
    }

    // ── getCoaches ────────────────────────────────────────────────────────────

    @Test
    void get_coaches_returns_200_list() throws Exception {
        given(coachAthleteService.getCoaches(ATHLETE_ID)).willReturn(List.of(sampleRelationship()));
        given(profileRepo.findByOwnerId(COACH_ID)).willReturn(Optional.empty());

        mvc.perform(get("/api/v1/coaching/coaches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].coachId").value(COACH_ID));
    }
}
