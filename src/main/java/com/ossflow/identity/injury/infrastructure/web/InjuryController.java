package com.ossflow.identity.injury.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.identity.injury.application.InjuryService;
import com.ossflow.identity.injury.domain.Injury;
import com.ossflow.identity.injury.infrastructure.web.dto.CreateInjuryRequest;
import com.ossflow.identity.injury.infrastructure.web.dto.InjuryResponse;
import com.ossflow.identity.injury.infrastructure.web.dto.UpdateInjuryRequest;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/identity/injuries")
@Validated
@RequiredArgsConstructor
public class InjuryController {

    private final InjuryService service;
    private final InjuryWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public List<InjuryResponse> list() {
        return service.findAll(currentOwner.id())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<InjuryResponse> create(@Valid @RequestBody CreateInjuryRequest req) {
        Injury toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        Injury created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/identity/injuries/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public InjuryResponse update(@PathVariable @Positive Long id,
                                 @Valid @RequestBody UpdateInjuryRequest req) {
        Injury replacement = mapper.fromUpdate(req).toBuilder().build();
        return mapper.toResponse(service.update(id, currentOwner.id(), replacement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }
}
