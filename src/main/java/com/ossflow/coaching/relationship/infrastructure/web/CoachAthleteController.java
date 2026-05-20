package com.ossflow.coaching.relationship.infrastructure.web;

import com.ossflow.coaching.relationship.application.AthleteProfileComposer;
import com.ossflow.coaching.relationship.application.CoachAthleteService;
import com.ossflow.coaching.relationship.infrastructure.web.dto.*;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching")
@RequiredArgsConstructor
public class CoachAthleteController {

    private final CoachAthleteService coachAthleteService;
    private final AthleteProfileComposer composer;

    @PostMapping("/memberships/redeem")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void redeem(@AuthenticationPrincipal AccountPrincipal principal,
                       @RequestBody @Valid RedeemInvitationRequest request) {
        coachAthleteService.redeemCode(request.code(), principal.id());
    }

    @DeleteMapping("/memberships/{athleteId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAthlete(@AuthenticationPrincipal AccountPrincipal principal,
                               @PathVariable Long athleteId) {
        coachAthleteService.removeAthleteFromCoach(principal.id(), athleteId);
    }

    @DeleteMapping("/memberships/leave/{coachId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveCoach(@AuthenticationPrincipal AccountPrincipal principal,
                            @PathVariable Long coachId) {
        coachAthleteService.leaveCoach(principal.id(), coachId);
    }

    @GetMapping("/athletes")
    @PreAuthorize("hasRole('COACH')")
    public List<AthleteListItemResponse> getAthletes(@AuthenticationPrincipal AccountPrincipal principal) {
        return coachAthleteService.getAthletesWithProfile(principal.id());
    }

    @GetMapping("/athletes/{athleteId}/summary")
    @PreAuthorize("hasRole('COACH')")
    public AthleteSummaryResponse getAthleteSummary(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long athleteId) {
        return composer.compose(principal.id(), athleteId);
    }

    @GetMapping("/coaches")
    @PreAuthorize("isAuthenticated()")
    public List<CoachListItemResponse> getCoaches(@AuthenticationPrincipal AccountPrincipal principal) {
        return coachAthleteService.getCoachesWithProfile(principal.id());
    }
}
