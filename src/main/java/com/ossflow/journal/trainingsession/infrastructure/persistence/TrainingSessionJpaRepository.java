package com.ossflow.journal.trainingsession.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingSessionJpaRepository extends JpaRepository<TrainingSessionEntity, Long> {

    Optional<TrainingSessionEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<TrainingSessionEntity> findByOwnerId(Long ownerId, Pageable pageable);
}
