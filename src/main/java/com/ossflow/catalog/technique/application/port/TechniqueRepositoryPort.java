package com.ossflow.catalog.technique.application.port;

import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TechniqueRepositoryPort {
    Technique save(Technique technique);
    Optional<Technique> findById(Long id, Long ownerId);
    Page<Technique> findAll(Long ownerId, TechniqueCategory category, Belt belt,
                            Modality modality, Long startPositionId, Long endPositionId,
                            Pageable pageable);
    boolean existsByName(Long ownerId, String name);
    void softDelete(Long id, Long ownerId);
    Optional<Technique> findInTrashById(Long id, Long ownerId);
    Technique restore(Long id, Long ownerId);
    Page<Technique> findTrash(Long ownerId, Pageable pageable);
    long countByPositionId(Long positionId);
}
