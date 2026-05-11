package com.ossflow.catalog.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.infrastructure.web.PositionWebMapper;
import com.ossflow.catalog.position.infrastructure.web.dto.PositionResponse;
import com.ossflow.catalog.technique.application.TechniqueService;
import com.ossflow.catalog.technique.infrastructure.web.TechniqueWebMapper;
import com.ossflow.catalog.technique.infrastructure.web.dto.TechniqueResponse;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/catalog/trash")
@Validated
@RequiredArgsConstructor
public class CatalogTrashController {

    private final PositionService positionService;
    private final PositionWebMapper positionMapper;
    private final TechniqueService techniqueService;
    private final TechniqueWebMapper techniqueMapper;
    private final CurrentOwner currentOwner;

    @GetMapping("/positions")
    public Page<PositionResponse> trashPositions(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return positionService.trash(currentOwner.id(), pageable).map(positionMapper::toResponse);
    }

    @GetMapping("/techniques")
    public Page<TechniqueResponse> trashTechniques(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return techniqueService.trash(currentOwner.id(), pageable).map(techniqueMapper::toResponse);
    }
}
