package com.ossflow.coaching.relationship.infrastructure.web;

import com.ossflow.coaching.invitation.application.CoachInvitationService;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.relationship.application.AthleteProfileComposer;
import com.ossflow.coaching.relationship.application.CoachAthleteService;
import com.ossflow.coaching.relationship.infrastructure.web.dto.*;
import com.ossflow.identity.auth.application.EmailOutboxService;
import com.ossflow.identity.auth.application.EmailService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching")
@RequiredArgsConstructor
public class CoachAthleteController {

    private final CoachAthleteService coachAthleteService;
    private final CoachInvitationService invitationService;
    private final AthleteProfileComposer composer;
    private final CoachingNotificationService notificationService;
    private final EmailOutboxService emailOutboxService;
    private final EmailService emailService;
    private final UserProfileRepositoryPort profileRepo;
    private final AccountRepositoryPort accountRepo;

    @PostMapping("/memberships/redeem")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void redeem(@AuthenticationPrincipal AccountPrincipal principal,
                       @RequestBody @Valid RedeemInvitationRequest request) {
        CoachInvitation inv = invitationService.validateCode(request.code());
        if (inv == null) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_CODE");

        try {
            coachAthleteService.link(inv.coachId(), principal.id(), inv.id());
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("ALREADY_LINKED")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "ALREADY_LINKED");
            }
            throw e;
        }
        invitationService.incrementUsedCount(inv);

        var athleteProfile = profileRepo.findByOwnerId(principal.id()).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "Un atleta";
        notificationService.notifyAthleteJoined(inv.coachId(), athleteName);

        accountRepo.findById(inv.coachId()).ifPresent(coachAccount ->
            emailOutboxService.enqueueCoachingEmail(
                inv.coachId(), coachAccount.email(),
                emailService.athleteJoinedSubject(),
                emailService.athleteJoinedBody(athleteName))
        );
    }

    @DeleteMapping("/memberships/{athleteId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAthlete(@AuthenticationPrincipal AccountPrincipal principal,
                               @PathVariable Long athleteId) {
        coachAthleteService.unlinkByCoach(principal.id(), athleteId);

        var athleteProfile = profileRepo.findByOwnerId(athleteId).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "El atleta";
        var coachProfile = profileRepo.findByOwnerId(principal.id()).orElse(null);
        String coachName = coachProfile != null ? coachProfile.displayName() : "Tu maestro";

        notificationService.notifyCoachRemovedYou(athleteId, coachName);
        accountRepo.findById(athleteId).ifPresent(athleteAccount ->
            emailOutboxService.enqueueCoachingEmail(
                athleteId, athleteAccount.email(),
                emailService.coachRemovedYouSubject(),
                emailService.coachRemovedYouBody(coachName))
        );
        notificationService.notifyAthleteLeft(principal.id(), athleteName);
    }

    @DeleteMapping("/memberships/leave/{coachId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveCoach(@AuthenticationPrincipal AccountPrincipal principal,
                            @PathVariable Long coachId) {
        coachAthleteService.unlinkByAthlete(principal.id(), coachId);

        var athleteProfile = profileRepo.findByOwnerId(principal.id()).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "Un atleta";

        notificationService.notifyAthleteLeft(coachId, athleteName);
        accountRepo.findById(coachId).ifPresent(coachAccount ->
            emailOutboxService.enqueueCoachingEmail(
                coachId, coachAccount.email(),
                emailService.athleteLeftSubject(),
                emailService.athleteLeftBody(athleteName))
        );
    }

    @GetMapping("/athletes")
    @PreAuthorize("hasRole('COACH')")
    public List<AthleteListItemResponse> getAthletes(@AuthenticationPrincipal AccountPrincipal principal) {
        return coachAthleteService.getAthletes(principal.id()).stream()
                .map(r -> {
                    var profile = profileRepo.findByOwnerId(r.athleteId()).orElse(null);
                    return AthleteListItemResponse.from(r, profile);
                }).toList();
    }

    @GetMapping("/athletes/{athleteId}/summary")
    @PreAuthorize("hasRole('COACH')")
    public AthleteSummaryResponse getAthleteSummary(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long athleteId) {
        return composer.compose(principal.id(), athleteId);
    }

    @GetMapping("/coaches")
    public List<CoachListItemResponse> getCoaches(@AuthenticationPrincipal AccountPrincipal principal) {
        return coachAthleteService.getCoaches(principal.id()).stream()
                .map(r -> {
                    var profile = profileRepo.findByOwnerId(r.coachId()).orElse(null);
                    return CoachListItemResponse.from(r, profile);
                }).toList();
    }
}
