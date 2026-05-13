package com.ossflow.coaching.notification.infrastructure.web;

import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.notification.infrastructure.web.dto.NotificationResponse;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching/notifications")
@RequiredArgsConstructor
public class CoachingNotificationController {

    private final CoachingNotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationResponse> getUnread(@AuthenticationPrincipal AccountPrincipal principal) {
        return notificationService.getUnread(principal.id())
                .stream().map(NotificationResponse::from).toList();
    }

    @PatchMapping("/read")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(@AuthenticationPrincipal AccountPrincipal principal) {
        notificationService.markAllRead(principal.id());
    }
}
