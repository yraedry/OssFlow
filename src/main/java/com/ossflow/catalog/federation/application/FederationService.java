package com.ossflow.catalog.federation.application;

import com.ossflow.catalog.federation.application.port.FederationRepositoryPort;
import com.ossflow.catalog.federation.domain.Federation;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FederationService {

    private final FederationRepositoryPort repository;

    public List<Federation> findAll() {
        return repository.findAll();
    }

    public Federation findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("FEDERATION_NOT_FOUND",
                        "No existe la federación con id %d".formatted(id),
                        Map.of("federationId", id)));
    }
}
