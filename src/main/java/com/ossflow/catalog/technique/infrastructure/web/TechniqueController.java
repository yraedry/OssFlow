package com.ossflow.catalog.technique.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.catalog.ruleset.application.RulesetService;
import com.ossflow.catalog.ruleset.domain.RulesetTechnique;
import com.ossflow.catalog.ruleset.infrastructure.web.RulesetWebMapper;
import com.ossflow.catalog.ruleset.infrastructure.web.dto.RulesetTechniqueResponse;
import com.ossflow.catalog.technique.application.TechniqueService;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import com.ossflow.catalog.technique.infrastructure.web.dto.*;
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
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/catalog/techniques")
@Validated
@RequiredArgsConstructor
public class TechniqueController {

    private final TechniqueService service;
    private final TechniqueWebMapper mapper;
    private final CurrentOwner currentOwner;
    private final RulesetService rulesetService;
    private final RulesetWebMapper rulesetMapper;

    @GetMapping
    public Page<TechniqueResponse> list(
            @RequestParam(required = false) TechniqueCategory category,
            @RequestParam(required = false) Belt belt,
            @RequestParam(required = false) Modality modality,
            @RequestParam(required = false) Long startPositionId,
            @RequestParam(required = false) Long endPositionId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), parseSort(sort));
        return service.list(currentOwner.id(), category, belt, modality,
                startPositionId, endPositionId, search, pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public TechniqueResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<TechniqueResponse> create(@Valid @RequestBody CreateTechniqueRequest req) {
        Technique toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        Technique created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/catalog/techniques/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public TechniqueResponse replace(@PathVariable @Positive Long id,
                                     @Valid @RequestBody UpdateTechniqueRequest req) {
        Technique replacement = Technique.builder()
                .name(req.name()).category(req.category())
                .description(req.description()).youtubeUrl(req.youtubeUrl())
                .minimumBelt(req.minimumBelt()).modality(req.modality())
                .startPositionId(req.startPositionId()).endPositionId(req.endPositionId())
                .visibility(req.visibility())
                .build();
        return mapper.toResponse(service.replace(id, currentOwner.id(), replacement));
    }

    @PatchMapping("/{id}")
    public TechniqueResponse patch(@PathVariable @Positive Long id,
                                   @Valid @RequestBody PatchTechniqueRequest req) {
        Technique existing = service.findById(id, currentOwner.id());
        Technique patched = existing.toBuilder()
                .name(req.name() != null ? req.name() : existing.name())
                .category(req.category() != null ? req.category() : existing.category())
                .family(req.family() != null ? req.family() : existing.family())
                .description(req.description() != null ? req.description() : existing.description())
                .youtubeUrl(req.youtubeUrl() != null ? req.youtubeUrl() : existing.youtubeUrl())
                .minimumBelt(req.minimumBelt() != null ? req.minimumBelt() : existing.minimumBelt())
                .modality(req.modality() != null ? req.modality() : existing.modality())
                .startPositionId(req.startPositionId() != null ? req.startPositionId() : existing.startPositionId())
                .endPositionId(req.endPositionId() != null ? req.endPositionId() : existing.endPositionId())
                .visibility(req.visibility() != null ? req.visibility() : existing.visibility())
                .build();
        return mapper.toResponse(service.patch(id, currentOwner.id(), patched));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public TechniqueResponse restore(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.restore(id, currentOwner.id()));
    }

    @GetMapping("/{id}/legality")
    public List<RulesetTechniqueResponse> legality(@PathVariable @Positive Long id) {
        return rulesetService.getLegalityForTechnique(id).stream()
                .map(rulesetMapper::toResponse).toList();
    }

    private static Sort parseSort(String s) {
        String[] parts = s.split(",");
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, parts[0]);
    }
}
