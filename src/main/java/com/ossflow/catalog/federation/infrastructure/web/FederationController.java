package com.ossflow.catalog.federation.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.catalog.federation.application.FederationService;
import com.ossflow.catalog.federation.infrastructure.web.dto.FederationResponse;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/catalog/federations")
@Validated
@RequiredArgsConstructor
public class FederationController {

    private final FederationService service;
    private final FederationWebMapper mapper;

    @GetMapping
    public List<FederationResponse> list() {
        return service.findAll().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public FederationResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id));
    }
}
