package com.ossflow.catalog.federation.application.port;

import com.ossflow.catalog.federation.domain.Federation;

import java.util.List;
import java.util.Optional;

public interface FederationRepositoryPort {
    List<Federation> findAll();
    Optional<Federation> findById(Long id);
}
