package com.ossflow.coaching.observation.infrastructure.web;

import com.ossflow.coaching.observation.application.CoachObservationService;
import com.ossflow.coaching.observation.domain.CoachObservation;
import com.ossflow.coaching.observation.infrastructure.web.dto.CreateObservationRequest;
import com.ossflow.coaching.observation.infrastructure.web.dto.ObservationResponse;
import com.ossflow.coaching.observation.infrastructure.web.dto.RadarPointResponse;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching/observations")
@RequiredArgsConstructor
public class CoachObservationController {

    private final CoachObservationService service;

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public ObservationResponse create(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody @Valid CreateObservationRequest request) {
        CoachObservation created = service.create(principal.id(), CoachObservation.builder()
                .athleteId(request.athleteId())
                .body(request.body())
                .tone(request.tone())
                .techniqueFamily(request.techniqueFamily())
                .build());
        return ObservationResponse.from(created);
    }

    @GetMapping("/athlete/{athleteId}")
    @PreAuthorize("hasRole('COACH')")
    public List<ObservationResponse> list(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long athleteId) {
        return service.list(principal.id(), athleteId).stream()
                .map(ObservationResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.delete(principal.id(), id);
    }

    @GetMapping("/athlete/{athleteId}/radar")
    @PreAuthorize("hasRole('COACH')")
    public List<RadarPointResponse> radar(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long athleteId) {
        return service.radar(principal.id(), athleteId).stream()
                .map(RadarPointResponse::from).toList();
    }
}
