package com.ossflow.catalog.position.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionJpaRepository extends JpaRepository<PositionEntity, Long> {

    Optional<PositionEntity> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT p FROM PositionEntity p WHERE (p.ownerId = :ownerId OR p.ownerId = 1)")
    Page<PositionEntity> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT p FROM PositionEntity p WHERE (p.ownerId = :ownerId OR p.ownerId = 1) AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<PositionEntity> findByOwnerIdAndNameContainingIgnoreCase(Long ownerId, String name, Pageable pageable);

    boolean existsByOwnerIdAndName(Long ownerId, String name);
}
