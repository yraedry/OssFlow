package com.ossflow.catalog.exercise.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.catalog.exercise.application.ExerciseService;
import com.ossflow.catalog.exercise.domain.EquipmentType;
import com.ossflow.catalog.exercise.domain.Exercise;
import com.ossflow.catalog.exercise.domain.ExerciseCategory;
import com.ossflow.catalog.exercise.infrastructure.web.dto.CreateExerciseRequest;
import com.ossflow.catalog.exercise.infrastructure.web.dto.ExerciseResponse;
import com.ossflow.catalog.exercise.infrastructure.web.dto.UpdateExerciseRequest;
import com.ossflow.catalog.position.domain.Visibility;
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

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/catalog/exercises")
@Validated
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService service;
    private final ExerciseWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<ExerciseResponse> list(
            @RequestParam(required = false) ExerciseCategory category,
            @RequestParam(required = false) EquipmentType equipment,
            @RequestParam(required = false) Visibility visibility,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), parseSort(sort));
        return service.list(currentOwner.id(), category, equipment, visibility, pageable)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public ExerciseResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody CreateExerciseRequest req) {
        Exercise toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        Exercise created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/catalog/exercises/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ExerciseResponse replace(@PathVariable @Positive Long id,
                                    @Valid @RequestBody UpdateExerciseRequest req) {
        Exercise replacement = mapper.fromUpdate(req).toBuilder().build();
        return mapper.toResponse(service.replace(id, currentOwner.id(), replacement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    private static Sort parseSort(String s) {
        String[] parts = s.split(",");
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, parts[0]);
    }
}
