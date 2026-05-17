package com.ossflow.coaching.privatesession.infrastructure.web;

import com.ossflow.coaching.privatesession.application.PrivateSessionService;
import com.ossflow.coaching.privatesession.infrastructure.web.dto.CreatePrivateSessionRequest;
import com.ossflow.coaching.privatesession.infrastructure.web.dto.PrivateSessionResponse;
import com.ossflow.coaching.privatesession.infrastructure.web.dto.UpdatePrivateSessionRequest;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching/private-sessions")
@RequiredArgsConstructor
public class PrivateSessionController {

    private final PrivateSessionService service;

    // /mine MUST be declared before /{id} to avoid Spring treating "mine" as an ID
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public List<PrivateSessionResponse> mine(
            @AuthenticationPrincipal AccountPrincipal principal) {
        return service.listMine(principal.id()).stream()
                .map(PrivateSessionResponse::from).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public PrivateSessionResponse create(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody @Valid CreatePrivateSessionRequest request) {
        return PrivateSessionResponse.from(service.create(principal.id(), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('COACH')")
    public List<PrivateSessionResponse> list(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestParam(required = false) Long athleteId) {
        if (athleteId != null) {
            return service.listByAthlete(principal.id(), athleteId).stream()
                    .map(PrivateSessionResponse::from).toList();
        }
        return service.listAll(principal.id()).stream()
                .map(PrivateSessionResponse::from).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public PrivateSessionResponse update(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id,
            @RequestBody UpdatePrivateSessionRequest request) {
        return PrivateSessionResponse.from(service.update(principal.id(), id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.delete(principal.id(), id);
    }
}
