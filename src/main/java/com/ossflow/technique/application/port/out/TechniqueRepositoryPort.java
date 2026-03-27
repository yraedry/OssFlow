package com.ossflow.technique.application.port.out;

import com.ossflow.technique.domain.model.Technique;
import java.util.Optional;

public interface TechniqueRepositoryPort {
    Technique save(Technique technique);
    Optional<Technique> findById(Long id);
}