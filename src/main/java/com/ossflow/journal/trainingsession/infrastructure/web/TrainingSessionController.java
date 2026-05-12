package com.ossflow.journal.trainingsession.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.journal.trainingsession.application.TrainingSessionService;
import com.ossflow.journal.trainingsession.domain.TrainingSession;
import com.ossflow.journal.trainingsession.domain.WorkedTechnique;
import com.ossflow.journal.trainingsession.infrastructure.web.dto.CreateTrainingSessionRequest;
import com.ossflow.journal.trainingsession.infrastructure.web.dto.TrainingSessionResponse;
import com.ossflow.journal.trainingsession.infrastructure.web.dto.UpsertWorkedTechniqueRequest;
import com.ossflow.journal.trainingsession.infrastructure.web.dto.WorkedTechniqueResponse;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/journal/training-sessions")
@Validated
@RequiredArgsConstructor
public class TrainingSessionController {

    private final TrainingSessionService service;
    private final TrainingSessionWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    @Transactional(readOnly = true)
    public Page<TrainingSessionResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "sessionDate"));
        return service.list(currentOwner.id(), pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public TrainingSessionResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<TrainingSessionResponse> create(@Valid @RequestBody CreateTrainingSessionRequest req) {
        TrainingSession toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        TrainingSession created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/journal/training-sessions/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public TrainingSessionResponse replace(@PathVariable @Positive Long id,
                                           @Valid @RequestBody CreateTrainingSessionRequest req) {
        TrainingSession replacement = mapper.fromCreate(req);
        return mapper.toResponse(service.replace(id, currentOwner.id(), replacement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public TrainingSessionResponse restore(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.restore(id, currentOwner.id()));
    }

    @PostMapping("/{id}/worked-techniques")
    public WorkedTechniqueResponse upsertWorkedTechnique(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpsertWorkedTechniqueRequest req) {
        WorkedTechnique wt = mapper.fromUpsertRequest(req).toBuilder()
                .trainingSessionId(id)
                .build();
        return mapper.toWorkedTechniqueResponse(service.upsertWorkedTechnique(id, currentOwner.id(), wt));
    }

    @DeleteMapping("/{id}/worked-techniques/{tid}")
    public ResponseEntity<Void> removeWorkedTechnique(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long tid) {
        service.removeWorkedTechnique(id, currentOwner.id(), tid);
        return ResponseEntity.noContent().build();
    }
}
