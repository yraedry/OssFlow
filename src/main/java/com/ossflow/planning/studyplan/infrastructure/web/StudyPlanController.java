package com.ossflow.planning.studyplan.infrastructure.web;

import com.ossflow.planning.studyplan.application.StudyPlanService;
import com.ossflow.planning.studyplan.domain.StudyPlan;
import com.ossflow.planning.studyplan.infrastructure.web.dto.CreateStudyPlanRequest;
import com.ossflow.planning.studyplan.infrastructure.web.dto.StudyPlanResponse;
import com.ossflow.planning.studyplan.infrastructure.web.dto.UpdateStudyPlanRequest;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/planning/study-plans")
@Validated
@RequiredArgsConstructor
public class StudyPlanController {

    private final StudyPlanService service;
    private final StudyPlanWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<StudyPlanResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), parseSort(sort));
        return service.list(currentOwner.id(), pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public StudyPlanResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<StudyPlanResponse> create(@Valid @RequestBody CreateStudyPlanRequest req) {
        StudyPlan toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        StudyPlan created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/planning/study-plans/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public StudyPlanResponse replace(@PathVariable @Positive Long id,
                                     @Valid @RequestBody UpdateStudyPlanRequest req) {
        StudyPlan replacement = mapper.fromUpdate(req);
        return mapper.toResponse(service.replace(id, currentOwner.id(), replacement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public StudyPlanResponse restore(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.restore(id, currentOwner.id()));
    }

    private static Sort parseSort(String s) {
        String[] parts = s.split(",");
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, parts[0]);
    }
}
