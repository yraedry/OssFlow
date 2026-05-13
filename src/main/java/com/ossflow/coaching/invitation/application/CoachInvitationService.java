package com.ossflow.coaching.invitation.application;

import com.ossflow.coaching.invitation.application.port.CoachInvitationRepositoryPort;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoachInvitationService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final long TTL_HOURS = 48;
    private final SecureRandom rng = new SecureRandom();
    private final CoachInvitationRepositoryPort repo;

    public CoachInvitation generate(Long coachId) {
        repo.findActiveByCoachId(coachId).ifPresent(existing ->
            repo.save(existing.toBuilder().status(InvitationStatus.REVOKED).build())
        );
        return repo.save(CoachInvitation.builder()
                .coachId(coachId)
                .code(generateCode())
                .status(InvitationStatus.PENDING)
                .usedCount(0)
                .expiresAt(Instant.now().plusSeconds(TTL_HOURS * 3600))
                .createdAt(Instant.now())
                .build());
    }

    public Optional<CoachInvitation> getActive(Long coachId) {
        return repo.findActiveByCoachId(coachId)
                .filter(inv -> inv.expiresAt().isAfter(Instant.now()));
    }

    public void revoke(Long coachId) {
        repo.findActiveByCoachId(coachId).ifPresent(inv ->
            repo.save(inv.toBuilder().status(InvitationStatus.REVOKED).build())
        );
    }

    public CoachInvitation validateCode(String code) {
        return repo.findByCode(code)
                .filter(inv -> inv.status() == InvitationStatus.PENDING)
                .filter(inv -> inv.expiresAt().isAfter(Instant.now()))
                .orElse(null);
    }

    public CoachInvitation incrementUsedCount(CoachInvitation invitation) {
        return repo.save(invitation.toBuilder().usedCount(invitation.usedCount() + 1).build());
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(rng.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
