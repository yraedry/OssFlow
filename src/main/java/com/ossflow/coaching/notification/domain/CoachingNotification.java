package com.ossflow.coaching.notification.domain;

import lombok.Builder;
import java.time.Instant;

@Builder(toBuilder = true)
public record CoachingNotification(
        Long id,
        Long recipientAccountId,
        NotificationType type,
        String payload,
        boolean read,
        Instant createdAt
) {}
