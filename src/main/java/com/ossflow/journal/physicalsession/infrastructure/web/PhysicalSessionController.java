package com.ossflow.journal.physicalsession.infrastructure.web;

import com.ossflow.journal.physicalsession.application.PhysicalSessionService;
import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.CreatePhysicalSessionRequest;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.PhysicalSessionResponse;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/journal/physical-sessions")
@Validated
@RequiredArgsConstructor
public class PhysicalSessionController {

    private final PhysicalSessionService service;
    private final PhysicalSessionWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<PhysicalSessionResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return service.list(
                currentOwner.id(),
                PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "sessionDate"))
        ).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public PhysicalSessionResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<PhysicalSessionResponse> create(@Valid @RequestBody CreatePhysicalSessionRequest req) {
        PhysicalSession created = service.create(
                mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build()
        );
        return ResponseEntity
                .created(URI.create("/api/v1/journal/physical-sessions/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public PhysicalSessionResponse replace(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CreatePhysicalSessionRequest req) {
        return mapper.toResponse(
                service.replace(id, currentOwner.id(), mapper.fromCreate(req))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }
}
