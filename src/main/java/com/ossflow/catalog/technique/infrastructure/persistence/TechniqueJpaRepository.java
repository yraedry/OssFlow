package com.ossflow.catalog.technique.infrastructure.persistence;

import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechniqueJpaRepository extends JpaRepository<TechniqueEntity, Long> {

    Optional<TechniqueEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<TechniqueEntity> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByOwnerIdAndName(Long ownerId, String name);

    long countByStartPositionIdOrEndPositionId(Long startId, Long endId);

    @Query("""
            SELECT t FROM TechniqueEntity t
            WHERE (t.ownerId = :ownerId OR t.ownerId = 1)
            AND (:category IS NULL OR t.category = :category)
            AND (:belt IS NULL OR t.minimumBelt = :belt)
            AND (:modality IS NULL OR t.modality = :modality)
            AND (:startPositionId IS NULL OR t.startPositionId = :startPositionId)
            AND (:endPositionId IS NULL OR t.endPositionId = :endPositionId)
            """)
    Page<TechniqueEntity> findByFilters(Long ownerId, TechniqueCategory category,
                                         Belt belt, Modality modality,
                                         Long startPositionId, Long endPositionId,
                                         Pageable pageable);
}
