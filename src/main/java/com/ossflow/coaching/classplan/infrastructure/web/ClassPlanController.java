package com.ossflow.coaching.classplan.infrastructure.web;

import com.ossflow.coaching.classplan.application.ClassPlanService;
import com.ossflow.coaching.classplan.infrastructure.web.dto.ClassPlanResponse;
import com.ossflow.coaching.classplan.infrastructure.web.dto.CreateClassPlanRequest;
import com.ossflow.coaching.classplan.infrastructure.web.dto.UpdateClassPlanRequest;
import com.ossflow.coaching.studyplan.application.CoachStudyPlanService;
import com.ossflow.coaching.studyplan.domain.CoachStudyBlock;
import com.ossflow.coaching.studyplan.domain.CoachStudyItem;
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
@RequestMapping("/api/v1/coaching/class-plans")
@RequiredArgsConstructor
public class ClassPlanController {

    private final ClassPlanService service;
    private final CoachStudyPlanService studyPlanService;

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public ClassPlanResponse create(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody @Valid CreateClassPlanRequest req) {
        return ClassPlanResponse.from(service.create(
                principal.id(), req.gymId(), req.title(), req.description(),
                req.scheduledDate(), req.durationMinutes(), req.modality()));
    }

    @GetMapping
    @PreAuthorize("hasRole('COACH')")
    public List<ClassPlanResponse> list(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestParam Long gymId) {
        return service.list(principal.id(), gymId)
                .stream().map(ClassPlanResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public ClassPlanResponse get(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        return ClassPlanResponse.from(service.get(id, principal.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public ClassPlanResponse update(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id,
            @RequestBody @Valid UpdateClassPlanRequest req) {
        return ClassPlanResponse.from(service.update(
                id, principal.id(), req.title(), req.description(),
                req.scheduledDate(), req.durationMinutes(), req.modality(), req.status()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.delete(id, principal.id());
    }

    // ─── Block sub-endpoints ────────────────────────────────────────────────

    @PostMapping("/{planId}/blocks")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyBlockResponse addBlock(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @RequestBody @Valid AddBlockRequest req) {
        return toBlockResponse(studyPlanService.addBlockToClassPlan(planId, principal.id(), req.title(), service));
    }

    @PatchMapping("/{planId}/blocks/{blockId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBlockTitle(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @RequestBody @Valid UpdateBlockTitleRequest req) {
        studyPlanService.updateBlockTitleInClassPlan(planId, blockId, principal.id(), req.title(), service);
    }

    @DeleteMapping("/{planId}/blocks/{blockId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlock(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId) {
        studyPlanService.deleteBlockFromClassPlan(planId, blockId, principal.id(), service);
    }

    @PostMapping("/{planId}/blocks/reorder")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderBlocks(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @RequestBody @Valid ReorderRequest req) {
        studyPlanService.reorderBlocksInClassPlan(planId, principal.id(), req.orderedIds(), service);
    }

    // ─── Item sub-endpoints ─────────────────────────────────────────────────

    @PostMapping("/{planId}/blocks/{blockId}/items/text")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyItemResponse addTextItem(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @RequestBody @Valid AddTextItemRequest req) {
        return toItemResponse(studyPlanService.addTextItemToClassPlan(planId, blockId, principal.id(), req.content(), service));
    }

    @PostMapping("/{planId}/blocks/{blockId}/items/technique")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyItemResponse addTechniqueItem(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @RequestBody @Valid AddTechniqueItemRequest req) {
        return toItemResponse(studyPlanService.addTechniqueItemToClassPlan(planId, blockId, principal.id(), req.techniqueId(), service));
    }

    @DeleteMapping("/{planId}/blocks/{blockId}/items/{itemId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @PathVariable Long itemId) {
        studyPlanService.deleteItemFromClassPlan(planId, blockId, itemId, principal.id(), service);
    }

    @PostMapping("/{planId}/blocks/{blockId}/items/reorder")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderItems(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long planId,
            @PathVariable Long blockId,
            @RequestBody @Valid ReorderRequest req) {
        studyPlanService.reorderItemsInClassPlan(planId, blockId, principal.id(), req.orderedIds(), service);
    }

    // ─── Mappers ────────────────────────────────────────────────────────────

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
