package com.ossflow.catalog.technique.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechniqueJpaRepository extends JpaRepository<TechniqueEntity, Long> {

    Optional<TechniqueEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<TechniqueEntity> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByOwnerIdAndName(Long ownerId, String name);

    long countByStartPositionIdOrEndPositionId(Long startId, Long endId);
}
