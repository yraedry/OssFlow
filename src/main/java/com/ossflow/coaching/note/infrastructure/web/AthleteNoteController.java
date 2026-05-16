package com.ossflow.coaching.note.infrastructure.web;

import com.ossflow.coaching.note.application.AthleteNoteService;
import com.ossflow.coaching.note.infrastructure.web.dto.CreateNoteRequest;
import com.ossflow.coaching.note.infrastructure.web.dto.NoteResponse;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching/notes")
@RequiredArgsConstructor
public class AthleteNoteController {

    private final AthleteNoteService service;

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody @Valid CreateNoteRequest request) {
        return NoteResponse.from(service.create(principal.id(), request));
    }

    @GetMapping("/athlete/{athleteId}")
    @PreAuthorize("hasRole('COACH')")
    public List<NoteResponse> listForCoach(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long athleteId) {
        return service.listForCoach(principal.id(), athleteId).stream()
                .map(NoteResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.softDelete(principal.id(), id);
    }

    @GetMapping("/received")
    @PreAuthorize("isAuthenticated()")
    public List<NoteResponse> listReceived(
            @AuthenticationPrincipal AccountPrincipal principal) {
        return service.listReceived(principal.id()).stream()
                .map(NoteResponse::from).toList();
    }

    @GetMapping("/received/unread-count")
    @PreAuthorize("isAuthenticated()")
    public Long countUnread(
            @AuthenticationPrincipal AccountPrincipal principal) {
        return service.countUnread(principal.id());
    }

    @GetMapping("/received/{id}")
    @PreAuthorize("isAuthenticated()")
    public NoteResponse getReceivedDetail(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        return NoteResponse.from(service.getReceivedDetail(principal.id(), id));
    }

    @PatchMapping("/received/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.markRead(principal.id(), id);
    }
}
