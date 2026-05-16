package com.ossflow.coaching.recommendation.infrastructure.web;

import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.coaching.recommendation.application.RecommendationService;
import com.ossflow.coaching.recommendation.domain.TechniqueRecommendation;
import com.ossflow.coaching.recommendation.infrastructure.web.dto.CreateRecommendationRequest;
import com.ossflow.coaching.recommendation.infrastructure.web.dto.RecommendationResponse;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService service;
    private final TechniqueRepositoryPort techniqueRepo;

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationResponse create(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody @Valid CreateRecommendationRequest request) {
        var saved = service.create(principal.id(), request);
        var technique = techniqueRepo.findById(saved.techniqueId(), principal.id()).orElse(null);
        return toResponse(saved, technique);
    }

    @GetMapping("/sent/athlete/{athleteId}")
    @PreAuthorize("hasRole('COACH')")
    public List<RecommendationResponse> listSent(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long athleteId) {
        var recs = service.listSent(principal.id(), athleteId);
        return enrich(recs);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.cancel(principal.id(), id);
    }

    @GetMapping("/received")
    @PreAuthorize("isAuthenticated()")
    public List<RecommendationResponse> listReceived(
            @AuthenticationPrincipal AccountPrincipal principal) {
        var recs = service.listReceived(principal.id());
        return enrich(recs);
    }

    @PatchMapping("/{id}/accept")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.accept(principal.id(), id);
    }

    @PatchMapping("/{id}/dismiss")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dismiss(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.dismiss(principal.id(), id);
    }

    // — helpers —

    private List<RecommendationResponse> enrich(List<TechniqueRecommendation> recs) {
        return recs.stream()
                .map(r -> toResponse(r, techniqueRepo.findById(r.techniqueId(), r.coachId()).orElse(null)))
                .toList();
    }

    private RecommendationResponse toResponse(TechniqueRecommendation r, Technique technique) {
        return new RecommendationResponse(
                r.id(),
                r.techniqueId(),
                technique != null ? technique.name() : null,
                technique != null && technique.family() != null ? technique.family().name() : null,
                r.note(),
                r.status() != null ? r.status().name() : null,
                r.recommendedAt(),
                r.resolvedAt()
        );
    }
}
