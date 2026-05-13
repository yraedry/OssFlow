package com.ossflow.coaching.invitation.infrastructure.web;

import com.ossflow.coaching.invitation.application.CoachInvitationService;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CoachInvitationControllerTest {

    @Mock CoachInvitationService invitationService;

    MockMvc mvc;
    static final long COACH_ID = 10L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setCoach(COACH_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new CoachInvitationController(invitationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private CoachInvitation sampleInvitation() {
        return CoachInvitation.builder()
                .id(1L)
                .coachId(COACH_ID)
                .code("ABC123")
                .status(InvitationStatus.PENDING)
                .usedCount(0)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void post_generate_returns_200_with_invitation_code() throws Exception {
        given(invitationService.generate(COACH_ID)).willReturn(sampleInvitation());

        mvc.perform(post("/api/v1/coaching/invitations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.usedCount").value(0));
    }

    @Test
    void get_active_returns_200_when_active_invitation_exists() throws Exception {
        given(invitationService.getActive(COACH_ID)).willReturn(sampleInvitation());

        mvc.perform(get("/api/v1/coaching/invitations/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ABC123"));
    }

    @Test
    void get_active_returns_204_when_no_active_invitation() throws Exception {
        given(invitationService.getActive(COACH_ID)).willReturn(null);

        mvc.perform(get("/api/v1/coaching/invitations/active"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_active_returns_204() throws Exception {
        doNothing().when(invitationService).revoke(COACH_ID);

        mvc.perform(delete("/api/v1/coaching/invitations/active"))
                .andExpect(status().isNoContent());
    }
}
