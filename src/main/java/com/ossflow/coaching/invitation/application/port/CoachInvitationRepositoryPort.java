package com.ossflow.coaching.invitation.application.port;

import com.ossflow.coaching.invitation.domain.CoachInvitation;
import java.util.Optional;

public interface CoachInvitationRepositoryPort {
    CoachInvitation save(CoachInvitation invitation);
    Optional<CoachInvitation> findActiveByCoachId(Long coachId);
    Optional<CoachInvitation> findByCode(String code);
}
