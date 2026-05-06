package com.ossflow.journal.trainingsession.application;

import com.ossflow.journal.trainingsession.application.port.TrainingSessionRepositoryPort;
import com.ossflow.journal.trainingsession.domain.TrainingSession;
import com.ossflow.journal.trainingsession.domain.WorkedTechnique;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingSessionService {

    private final TrainingSessionRepositoryPort repository;

    public TrainingSession create(TrainingSession session) {
        TrainingSession saved = repository.save(session);
        log.info("TrainingSession creada id={}", saved.id());
        return saved;
    }

    public TrainingSession findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("TRAINING_SESSION_NOT_FOUND",
                        "No existe la sesión con id %d".formatted(id),
                        Map.of("sessionId", id)));
    }

    public Page<TrainingSession> list(Long ownerId, Pageable pageable) {
        return repository.findAll(ownerId, pageable);
    }

    public TrainingSession replace(Long id, Long ownerId, TrainingSession replacement) {
        TrainingSession existing = findById(id, ownerId);
        TrainingSession toSave = replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build();
        return repository.save(toSave);
    }

    public TrainingSession patch(Long id, Long ownerId, TrainingSession patched) {
        return repository.save(patched.toBuilder()
                .id(id)
                .ownerId(ownerId)
                .build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("TrainingSession soft-deleted id={}", id);
    }

    public TrainingSession restore(Long id, Long ownerId) {
        TrainingSession restored = repository.restore(id, ownerId);
        log.info("TrainingSession restaurada id={}", id);
        return restored;
    }

    public Page<TrainingSession> trash(Long ownerId, Pageable pageable) {
        return repository.findTrash(ownerId, pageable);
    }

    public WorkedTechnique upsertWorkedTechnique(Long sessionId, Long ownerId, WorkedTechnique wt) {
        return repository.upsertWorkedTechnique(sessionId, ownerId, wt);
    }

    public void removeWorkedTechnique(Long sessionId, Long ownerId, Long techniqueId) {
        repository.removeWorkedTechnique(sessionId, ownerId, techniqueId);
        log.info("WorkedTechnique removed sessionId={} techniqueId={}", sessionId, techniqueId);
    }
}
