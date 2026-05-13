package com.ossflow.coaching.invitation.infrastructure.web;

import com.ossflow.coaching.invitation.application.CoachInvitationService;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.infrastructure.web.dto.InvitationCodeResponse;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coaching/invitations")
@RequiredArgsConstructor
public class CoachInvitationController {

    private final CoachInvitationService invitationService;

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<InvitationCodeResponse> generate(
            @AuthenticationPrincipal AccountPrincipal principal) {
        CoachInvitation inv = invitationService.generate(principal.id());
        return ResponseEntity.ok(InvitationCodeResponse.from(inv));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<InvitationCodeResponse> getActive(
            @AuthenticationPrincipal AccountPrincipal principal) {
        CoachInvitation inv = invitationService.getActive(principal.id());
        if (inv == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(InvitationCodeResponse.from(inv));
    }

    @DeleteMapping("/active")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@AuthenticationPrincipal AccountPrincipal principal) {
        invitationService.revoke(principal.id());
    }
}
