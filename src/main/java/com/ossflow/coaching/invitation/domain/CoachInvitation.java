package com.ossflow.coaching.invitation.domain;

import lombok.Builder;
import java.time.Instant;

@Builder(toBuilder = true)
public record CoachInvitation(
        Long id,
        Long coachId,
        String code,
        InvitationStatus status,
        int usedCount,
        Instant expiresAt,
        Instant createdAt
) {}
