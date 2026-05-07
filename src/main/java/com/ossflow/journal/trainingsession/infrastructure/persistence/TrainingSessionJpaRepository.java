package com.ossflow.journal.trainingsession.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TrainingSessionJpaRepository extends JpaRepository<TrainingSessionEntity, Long> {

    Optional<TrainingSessionEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<TrainingSessionEntity> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM TrainingSessionEntity t WHERE t.ownerId = :ownerId AND t.sessionDate >= :start AND t.sessionDate <= :end")
    long countByOwnerIdAndSessionDateBetween(
            @Param("ownerId") Long ownerId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
