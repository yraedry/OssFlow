package com.ossflow.journal.trainingsession.application.port;

import com.ossflow.journal.trainingsession.domain.TrainingSession;
import com.ossflow.journal.trainingsession.domain.WorkedTechnique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface TrainingSessionRepositoryPort {
    TrainingSession save(TrainingSession session);
    Optional<TrainingSession> findById(Long id, Long ownerId);
    Page<TrainingSession> findAll(Long ownerId, Pageable pageable);
    void softDelete(Long id, Long ownerId);
    TrainingSession restore(Long id, Long ownerId);
    Page<TrainingSession> findTrash(Long ownerId, Pageable pageable);
    WorkedTechnique upsertWorkedTechnique(Long sessionId, Long ownerId, WorkedTechnique wt);
    void removeWorkedTechnique(Long sessionId, Long ownerId, Long techniqueId);
    long countByOwnerAndWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd);
}
