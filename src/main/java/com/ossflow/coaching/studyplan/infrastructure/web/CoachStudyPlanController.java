package com.ossflow.coaching.studyplan.infrastructure.web;

import com.ossflow.coaching.studyplan.application.CoachStudyPlanService;
import com.ossflow.coaching.studyplan.domain.*;
import com.ossflow.coaching.studyplan.infrastructure.web.dto.*;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching/study-plans")
@RequiredArgsConstructor
public class CoachStudyPlanController {

    private final CoachStudyPlanService service;

    // ─── Coach endpoints ────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyPlanResponse create(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody @Valid CreatePlanRequest req) {
        return toResponse(service.createPlan(principal.id(), req.athleteId(), req.title()));
    }

    @GetMapping("/athlete/{athleteId}")
    @PreAuthorize("hasRole('COACH')")
    public List<StudyPlanResponse> listForCoach(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long athleteId) {
        return service.listForCoach(principal.id(), athleteId)
                .stream().map(this::toResponse).toList();
    }

    @GetMapping("/{planId}")
    @PreAuthorize("hasRole('COACH')")
    public StudyPlanResponse getForCoach(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId) {
        return toResponse(service.getPlan(planId, principal.id(), true));
    }

    @PatchMapping("/{planId}/content")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateContent(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @RequestBody @Valid UpdatePlanContentRequest req) {
        service.updatePlanContent(planId, principal.id(), req);
    }

    @PostMapping("/{planId}/duplicate")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyPlanResponse duplicatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody DuplicatePlanRequest request,
            @AuthenticationPrincipal AccountPrincipal principal) {
        return toResponse(service.duplicatePlan(planId, principal.id(), request.targetAthleteId()));
    }

    @PostMapping("/{planId}/publish")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publish(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId) {
        service.publishPlan(planId, principal.id());
    }

    @PostMapping("/{planId}/unpublish")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unpublish(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId) {
        service.unpublishPlan(planId, principal.id());
    }

    @DeleteMapping("/{planId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId) {
        service.deletePlan(planId, principal.id());
    }

    @PostMapping("/{planId}/blocks")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyBlockResponse addBlock(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @RequestBody @Valid AddBlockRequest req) {
        return toBlockResponse(service.addBlock(planId, principal.id(), req.title()));
    }

    @PatchMapping("/{planId}/blocks/{blockId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBlockTitle(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @Valid @RequestBody UpdateBlockTitleRequest request) {
        service.updateBlockTitle(planId, blockId, principal.id(), request.title());
    }

    @DeleteMapping("/{planId}/blocks/{blockId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlock(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId) {
        service.deleteBlock(planId, blockId, principal.id());
    }

    @PostMapping("/{planId}/blocks/reorder")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderBlocks(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @RequestBody @Valid ReorderRequest req) {
        service.reorderBlocks(planId, principal.id(), req.orderedIds());
    }

    @PostMapping("/{planId}/blocks/{blockId}/items/text")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyItemResponse addTextItem(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @RequestBody @Valid AddTextItemRequest req) {
        return toItemResponse(service.addTextItem(planId, blockId, principal.id(), req.content()));
    }

    @PostMapping("/{planId}/blocks/{blockId}/items/technique")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyItemResponse addTechniqueItem(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @RequestBody @Valid AddTechniqueItemRequest req) {
        return toItemResponse(service.addTechniqueItem(planId, blockId, principal.id(), req.techniqueId()));
    }

    @DeleteMapping("/{planId}/blocks/{blockId}/items/{itemId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @PathVariable Long itemId) {
        service.deleteItem(planId, blockId, itemId, principal.id());
    }

    @PostMapping("/{planId}/blocks/{blockId}/items/reorder")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderItems(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @RequestBody @Valid ReorderRequest req) {
        service.reorderItems(planId, blockId, principal.id(), req.orderedIds());
    }

    // ─── Athlete endpoints ──────────────────────────────────────────────────

    @GetMapping("/received")
    @PreAuthorize("isAuthenticated()")
    public List<StudyPlanResponse> listReceivedPlans(
            @AuthenticationPrincipal AccountPrincipal principal) {
        return service.listPublishedForAthlete(principal.id())
                .stream().map(this::toResponse).toList();
    }

    @GetMapping("/received/{planId}")
    @PreAuthorize("isAuthenticated()")
    public StudyPlanResponse getReceivedPlan(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId) {
        return toResponse(service.getPlan(planId, principal.id(), false));
    }

    // ─── Mappers ────────────────────────────────────────────────────────────

    private StudyPlanResponse toResponse(CoachStudyPlan p) {
        var blocks = p.blocks() != null
                ? p.blocks().stream().map(this::toBlockResponse).toList()
                : List.<StudyBlockResponse>of();
        return new StudyPlanResponse(p.id(), p.coachId(), p.athleteId(),
                p.title(), p.description(),
                p.status() != null ? p.status().name() : null,
                p.viewedByAthlete(), blocks, p.createdAt(), p.updatedAt());
    }

    private StudyBlockResponse toBlockResponse(CoachStudyBlock b) {
        var items = b.items() != null
                ? b.items().stream().map(this::toItemResponse).toList()
                : List.<StudyItemResponse>of();
        return new StudyBlockResponse(b.id(), b.title(), b.blockOrder(), items);
    }

    private StudyItemResponse toItemResponse(CoachStudyItem i) {
        return new StudyItemResponse(i.id(), i.itemOrder(),
                i.itemType() != null ? i.itemType().name() : null,
                i.content(), i.techniqueId(), i.techniqueName());
    }
}
