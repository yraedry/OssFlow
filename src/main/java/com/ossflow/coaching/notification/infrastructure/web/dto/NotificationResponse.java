package com.ossflow.coaching.notification.infrastructure.web.dto;

import com.ossflow.coaching.notification.domain.CoachingNotification;
import java.time.Instant;

public record NotificationResponse(Long id, String type, String payload, boolean read, Instant createdAt) {
    public static NotificationResponse from(CoachingNotification n) {
        return new NotificationResponse(n.id(), n.type().name(), n.payload(), n.read(), n.createdAt());
    }
}
