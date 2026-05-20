package com.ossflow.coaching.invitation.infrastructure.persistence;

import com.ossflow.coaching.invitation.application.port.CoachInvitationRepositoryPort;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoachInvitationPersistenceAdapter implements CoachInvitationRepositoryPort {
    private final CoachInvitationJpaRepository jpa;
    private final CoachInvitationPersistenceMapper mapper;

    @Override
    public CoachInvitation save(CoachInvitation invitation) {
        return mapper.toDomain(jpa.save(mapper.toEntity(invitation)));
    }

    @Override
    public Optional<CoachInvitation> findActiveByCoachId(Long coachId) {
        return jpa.findByCoachIdAndStatus(coachId, InvitationStatus.PENDING).map(mapper::toDomain);
    }

    @Override
    public Optional<CoachInvitation> findByCode(String code) {
        return jpa.findByCode(code).map(mapper::toDomain);
    }
}
