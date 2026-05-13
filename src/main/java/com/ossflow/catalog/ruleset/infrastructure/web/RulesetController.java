package com.ossflow.catalog.ruleset.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.catalog.ruleset.application.RulesetService;
import com.ossflow.catalog.ruleset.domain.Ruleset;
import com.ossflow.catalog.ruleset.domain.RulesetTechnique;
import com.ossflow.catalog.ruleset.infrastructure.web.dto.*;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/catalog/rulesets")
@Validated
@RequiredArgsConstructor
public class RulesetController {

    private final RulesetService service;
    private final RulesetWebMapper mapper;

    @GetMapping
    public Page<RulesetResponse> list(
            @RequestParam(required = false) Long federationId,
            @RequestParam(required = false) Belt belt,
            @RequestParam(required = false) Modality modality,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return service.list(federationId, belt, modality, pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public RulesetResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<RulesetResponse> create(@Valid @RequestBody CreateRulesetRequest req) {
        Ruleset created = service.create(mapper.fromCreate(req));
        return ResponseEntity
                .created(URI.create("/api/v1/catalog/rulesets/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PostMapping("/{id}/techniques")
    public RulesetTechniqueResponse upsertTechnique(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpsertRulesetTechniqueRequest req) {
        RulesetTechnique technique = mapper.fromUpsert(req).toBuilder().rulesetId(id).build();
        return mapper.toResponse(service.upsertTechnique(id, technique));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/techniques/{tid}")
    public ResponseEntity<Void> removeTechnique(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long tid) {
        service.removeTechnique(id, tid);
        return ResponseEntity.noContent().build();
    }
}
