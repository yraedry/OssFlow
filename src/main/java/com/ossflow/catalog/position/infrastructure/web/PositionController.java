package com.ossflow.catalog.position.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.infrastructure.web.dto.*;
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
@RequestMapping("/api/v1/catalog/positions")
@Validated
@RequiredArgsConstructor
public class PositionController {

    private final PositionService service;
    private final PositionWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<PositionResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), parseSort(sort));
        return service.list(currentOwner.id(), name, pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public PositionResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<PositionResponse> create(@Valid @RequestBody CreatePositionRequest req) {
        Position toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        Position created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/catalog/positions/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public PositionResponse replace(@PathVariable @Positive Long id,
                                    @Valid @RequestBody UpdatePositionRequest req) {
        Position replacement = Position.builder()
                .name(req.name()).type(req.type())
                .description(req.description()).youtubeUrl(req.youtubeUrl())
                .visibility(req.visibility())
                .build();
        return mapper.toResponse(service.replace(id, currentOwner.id(), replacement));
    }

    @PatchMapping("/{id}")
    public PositionResponse patch(@PathVariable @Positive Long id,
                                  @Valid @RequestBody PatchPositionRequest req) {
        Position existing = service.findById(id, currentOwner.id());
        Position patched = mapper.applyPatch(req, existing);
        return mapper.toResponse(service.patch(id, currentOwner.id(), patched));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public PositionResponse restore(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.restore(id, currentOwner.id()));
    }

    private static Sort parseSort(String s) {
        String[] parts = s.split(",");
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, parts[0]);
    }
}
