package com.ossflow.catalog.federation.infrastructure.persistence;

import com.ossflow.catalog.federation.application.port.FederationRepositoryPort;
import com.ossflow.catalog.federation.domain.Federation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FederationPersistenceAdapter implements FederationRepositoryPort {

    private final FederationJpaRepository repository;
    private final FederationPersistenceMapper mapper;

    @Override
    public List<Federation> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Federation> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
