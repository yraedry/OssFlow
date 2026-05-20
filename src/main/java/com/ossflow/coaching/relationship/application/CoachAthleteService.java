package com.ossflow.coaching.relationship.application;

import com.ossflow.coaching.invitation.application.CoachInvitationService;
import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import com.ossflow.coaching.relationship.infrastructure.web.dto.AthleteListItemResponse;
import com.ossflow.coaching.relationship.infrastructure.web.dto.CoachListItemResponse;
import com.ossflow.identity.auth.application.EmailOutboxService;
import com.ossflow.identity.auth.application.EmailService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachAthleteService {

    private final CoachAthleteRepositoryPort repo;
    private final CoachInvitationService invitationService;
    private final CoachingNotificationService notificationService;
    private final EmailOutboxService emailOutboxService;
    private final EmailService emailService;
    private final UserProfileRepositoryPort profileRepo;
    private final AccountRepositoryPort accountRepo;

    // ── Orchestration methods ──────────────────────────────────────────────────

    @Transactional
    public void redeemCode(String code, Long athleteId) {
        var inv = invitationService.validateCode(code);
        if (inv == null) {
            throw new com.ossflow.shared.exception.UnprocessableException("INVALID_CODE", "Código de invitación inválido o expirado");
        }
        if (inv.coachId().equals(athleteId)) {
            throw new com.ossflow.shared.exception.UnprocessableException("SELF_LINK", "No puedes vincularte a ti mismo como atleta");
        }
        link(inv.coachId(), athleteId, inv.id());
        invitationService.incrementUsedCount(inv);

        var athleteProfile = profileRepo.findByOwnerId(athleteId).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "Un atleta";
        notificationService.notifyAthleteJoined(inv.coachId(), athleteName);

        accountRepo.findById(inv.coachId()).ifPresent(coachAccount ->
            emailOutboxService.enqueueCoachingEmail(
                inv.coachId(), coachAccount.email(),
                emailService.athleteJoinedSubject(),
                emailService.athleteJoinedBody(athleteName))
        );
    }

    public void removeAthleteFromCoach(Long coachId, Long athleteId) {
        unlinkByCoach(coachId, athleteId);

        var athleteProfile = profileRepo.findByOwnerId(athleteId).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "El atleta";
        var coachProfile = profileRepo.findByOwnerId(coachId).orElse(null);
        String coachName = coachProfile != null ? coachProfile.displayName() : "Tu maestro";

        notificationService.notifyCoachRemovedYou(athleteId, coachName);
        accountRepo.findById(athleteId).ifPresent(athleteAccount ->
            emailOutboxService.enqueueCoachingEmail(
                athleteId, athleteAccount.email(),
                emailService.coachRemovedYouSubject(),
                emailService.coachRemovedYouBody(coachName))
        );
        notificationService.notifyAthleteLeft(coachId, athleteName);
    }

    public void leaveCoach(Long athleteId, Long coachId) {
        unlinkByAthlete(athleteId, coachId);

        var athleteProfile = profileRepo.findByOwnerId(athleteId).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "Un atleta";

        notificationService.notifyAthleteLeft(coachId, athleteName);
        accountRepo.findById(coachId).ifPresent(coachAccount ->
            emailOutboxService.enqueueCoachingEmail(
                coachId, coachAccount.email(),
                emailService.athleteLeftSubject(),
                emailService.athleteLeftBody(athleteName))
        );
    }

    // ── Core domain methods ────────────────────────────────────────────────────

    public CoachAthleteRelationship link(Long coachId, Long athleteId, Long invitationId) {
        if (repo.findByCoachIdAndAthleteId(coachId, athleteId).isPresent()) {
            throw new ConflictException("ALREADY_LINKED", "El atleta ya está vinculado a este maestro");
        }
        return repo.save(CoachAthleteRelationship.builder()
                .coachId(coachId)
                .athleteId(athleteId)
                .invitationId(invitationId)
                .linkedAt(Instant.now())
                .build());
    }

    public void unlinkByCoach(Long coachId, Long athleteId) {
        repo.deleteByCoachIdAndAthleteId(coachId, athleteId);
    }

    public void unlinkByAthlete(Long athleteId, Long coachId) {
        repo.deleteByCoachIdAndAthleteId(coachId, athleteId);
    }

    public List<CoachAthleteRelationship> getAthletes(Long coachId) {
        return repo.findAllByCoachId(coachId);
    }

    public List<CoachAthleteRelationship> getCoaches(Long athleteId) {
        return repo.findAllByAthleteId(athleteId);
    }

    public List<AthleteListItemResponse> getAthletesWithProfile(Long coachId) {
        return repo.findAllByCoachId(coachId).stream()
            .map(r -> {
                var profile = profileRepo.findByOwnerId(r.athleteId()).orElse(null);
                return AthleteListItemResponse.from(r, profile);
            }).toList();
    }

    public List<CoachListItemResponse> getCoachesWithProfile(Long athleteId) {
        return repo.findAllByAthleteId(athleteId).stream()
            .map(r -> {
                var profile = profileRepo.findByOwnerId(r.coachId()).orElse(null);
                return CoachListItemResponse.from(r, profile);
            }).toList();
    }

    public boolean isLinked(Long coachId, Long athleteId) {
        return repo.existsByCoachIdAndAthleteId(coachId, athleteId);
    }
}
