package com.ossflow.journal.physicalsession.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PhysicalSessionJpaRepository extends JpaRepository<PhysicalSessionEntity, Long> {

    Page<PhysicalSessionEntity> findByOwnerId(Long ownerId, Pageable pageable);

    Optional<PhysicalSessionEntity> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT COUNT(p) FROM PhysicalSessionEntity p WHERE p.ownerId = :ownerId AND p.sessionDate >= :start AND p.sessionDate <= :end")
    long countByOwnerIdAndSessionDateBetween(
            @Param("ownerId") Long ownerId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
