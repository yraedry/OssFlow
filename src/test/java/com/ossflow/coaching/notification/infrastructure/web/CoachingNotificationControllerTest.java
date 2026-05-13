package com.ossflow.coaching.notification.infrastructure.web;

import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.notification.domain.CoachingNotification;
import com.ossflow.coaching.notification.domain.NotificationType;
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
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CoachingNotificationControllerTest {

    @Mock CoachingNotificationService notificationService;

    MockMvc mvc;
    static final long USER_ID = 42L;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setOwner(USER_ID);
        mvc = MockMvcBuilders
                .standaloneSetup(new CoachingNotificationController(notificationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    private CoachingNotification sampleNotification() {
        return CoachingNotification.builder()
                .id(1L)
                .recipientAccountId(USER_ID)
                .type(NotificationType.ATHLETE_JOINED)
                .payload("{\"athleteName\":\"Carlos\"}")
                .read(false)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void get_unread_returns_200_with_list() throws Exception {
        given(notificationService.getUnread(USER_ID)).willReturn(List.of(sampleNotification()));

        mvc.perform(get("/api/v1/coaching/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("ATHLETE_JOINED"))
                .andExpect(jsonPath("$[0].payload").value("{\"athleteName\":\"Carlos\"}"));
    }

    @Test
    void get_unread_returns_200_with_empty_list() throws Exception {
        given(notificationService.getUnread(USER_ID)).willReturn(List.of());

        mvc.perform(get("/api/v1/coaching/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void patch_read_returns_204_and_marks_all_read() throws Exception {
        mvc.perform(patch("/api/v1/coaching/notifications/read"))
                .andExpect(status().isNoContent());

        verify(notificationService).markAllRead(USER_ID);
    }
}
