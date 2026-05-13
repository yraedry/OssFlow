package com.ossflow.coaching.relationship.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.coaching.invitation.application.CoachInvitationService;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.relationship.application.AthleteProfileComposer;
import com.ossflow.coaching.relationship.application.CoachAthleteService;
import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import com.ossflow.coaching.relationship.infrastructure.web.dto.AthleteSummaryResponse;
import com.ossflow.coaching.relationship.infrastructure.web.dto.RedeemInvitationRequest;
import com.ossflow.identity.auth.application.EmailOutboxService;
import com.ossflow.identity.auth.application.EmailService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.AccountRole;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CoachAthleteControllerTest {

    @Mock CoachAthleteService coachAthleteService;
    @Mock CoachInvitationService invitationService;
    @Mock AthleteProfileComposer composer;
    @Mock CoachingNotificationService notificationService;
    @Mock EmailOutboxService emailOutboxService;
    @Mock EmailService emailService;
    @Mock UserProfileRepositoryPort profileRepo;
    @Mock AccountRepositoryPort accountRepo;

    MockMvc mvc;
    ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    static final long ATHLETE_ID = 42L;
    static final long COACH_ID = 10L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setOwner(ATHLETE_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new CoachAthleteController(
                        coachAthleteService, invitationService, composer,
                        notificationService, emailOutboxService, emailService,
                        profileRepo, accountRepo))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private CoachInvitation sampleInvitation() {
        return CoachInvitation.builder()
                .id(1L).coachId(COACH_ID).code("ABC123")
                .status(InvitationStatus.PENDING)
                .usedCount(0)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .build();
    }

    private CoachAthleteRelationship sampleRelationship() {
        return CoachAthleteRelationship.builder()
                .id(1L).coachId(COACH_ID).athleteId(ATHLETE_ID)
                .invitationId(1L).linkedAt(Instant.now()).build();
    }

    private Account sampleAccount(Long id, String email) {
        return new Account(id, email, null, AccountProvider.LOCAL, null, true, 0, AccountRole.ATHLETE, null, null);
    }

    // ── redeem ────────────────────────────────────────────────────────────────

    @Test
    void redeem_returns_204_on_valid_code() throws Exception {
        var inv = sampleInvitation();
        given(invitationService.validateCode("ABC123")).willReturn(inv);
        given(coachAthleteService.link(COACH_ID, ATHLETE_ID, 1L)).willReturn(sampleRelationship());
        given(invitationService.incrementUsedCount(inv)).willReturn(inv);
        given(profileRepo.findByOwnerId(ATHLETE_ID)).willReturn(Optional.empty());
        var coachAccount = sampleAccount(COACH_ID, "coach@example.com");
        given(accountRepo.findById(COACH_ID)).willReturn(Optional.of(coachAccount));
        given(emailService.athleteJoinedSubject()).willReturn("Nuevo alumno");
        given(emailService.athleteJoinedBody(anyString())).willReturn("<html>...</html>");

        mvc.perform(post("/api/v1/coaching/memberships/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new RedeemInvitationRequest("ABC123"))))
                .andExpect(status().isNoContent());

        verify(coachAthleteService).link(COACH_ID, ATHLETE_ID, 1L);
    }

    @Test
    void redeem_returns_422_when_code_invalid() throws Exception {
        given(invitationService.validateCode("ZZZZZZ")).willReturn(null);

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
        var inv = sampleInvitation();
        given(invitationService.validateCode("ABC123")).willReturn(inv);
        given(coachAthleteService.link(COACH_ID, ATHLETE_ID, 1L))
                .willThrow(new IllegalStateException("ALREADY_LINKED: El atleta ya está vinculado"));

        mvc.perform(post("/api/v1/coaching/memberships/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new RedeemInvitationRequest("ABC123"))))
                .andExpect(status().isConflict());
    }

    // ── removeAthlete ─────────────────────────────────────────────────────────

    @Test
    void remove_athlete_returns_204() throws Exception {
        TestSecurityContext.setCoach(COACH_ID);
        given(profileRepo.findByOwnerId(ATHLETE_ID)).willReturn(Optional.empty());
        given(profileRepo.findByOwnerId(COACH_ID)).willReturn(Optional.empty());
        given(accountRepo.findById(ATHLETE_ID)).willReturn(Optional.empty());

        mvc.perform(delete("/api/v1/coaching/memberships/" + ATHLETE_ID))
                .andExpect(status().isNoContent());

        verify(coachAthleteService).unlinkByCoach(COACH_ID, ATHLETE_ID);
    }

    // ── leaveCoach ────────────────────────────────────────────────────────────

    @Test
    void leave_coach_returns_204() throws Exception {
        given(profileRepo.findByOwnerId(ATHLETE_ID)).willReturn(Optional.empty());
        given(accountRepo.findById(COACH_ID)).willReturn(Optional.empty());

        mvc.perform(delete("/api/v1/coaching/memberships/leave/" + COACH_ID))
                .andExpect(status().isNoContent());

        verify(coachAthleteService).unlinkByAthlete(ATHLETE_ID, COACH_ID);
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
