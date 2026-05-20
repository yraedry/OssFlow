package com.ossflow.coaching.invitation.infrastructure.web.dto;

import com.ossflow.coaching.invitation.domain.CoachInvitation;
import java.time.Instant;

public record InvitationCodeResponse(String code, Instant expiresAt, int usedCount) {
    public static InvitationCodeResponse from(CoachInvitation inv) {
        return new InvitationCodeResponse(inv.code(), inv.expiresAt(), inv.usedCount());
    }
}
