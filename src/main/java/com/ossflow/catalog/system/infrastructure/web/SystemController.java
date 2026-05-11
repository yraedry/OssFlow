package com.ossflow.catalog.system.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.catalog.system.application.SystemService;
import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.catalog.system.infrastructure.web.dto.*;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/catalog/systems")
@Validated
@RequiredArgsConstructor
public class SystemController {

    private final SystemService service;
    private final SystemWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<SystemResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return service.list(currentOwner.id(), PageRequest.of(page, Math.min(size, 100)))
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public SystemResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<SystemResponse> create(@Valid @RequestBody CreateSystemRequest req) {
        OssSystem toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        OssSystem created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/catalog/systems/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public SystemResponse replace(@PathVariable @Positive Long id,
                                  @Valid @RequestBody CreateSystemRequest req) {
        OssSystem replacement = mapper.fromCreate(req);
        return mapper.toResponse(service.replace(id, currentOwner.id(), replacement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public SystemResponse restore(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.restore(id, currentOwner.id()));
    }
}
