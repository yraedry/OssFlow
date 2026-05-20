package com.ossflow.coaching.invitation.infrastructure.persistence;

import com.ossflow.coaching.invitation.domain.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CoachInvitationJpaRepository extends JpaRepository<CoachInvitationEntity, Long> {
    Optional<CoachInvitationEntity> findByCoachIdAndStatus(Long coachId, InvitationStatus status);
    Optional<CoachInvitationEntity> findByCode(String code);
}
