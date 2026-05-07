package com.ossflow.journal.trainingsession.infrastructure.persistence;

import com.ossflow.journal.trainingsession.application.port.TrainingSessionRepositoryPort;
import com.ossflow.journal.trainingsession.domain.TrainingSession;
import com.ossflow.journal.trainingsession.domain.WorkedTechnique;
import com.ossflow.shared.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainingSessionPersistenceAdapter implements TrainingSessionRepositoryPort {

    private final TrainingSessionJpaRepository repository;
    private final TrainingSessionPersistenceMapper mapper;
    private final EntityManager em;

    @Override
    @Transactional
    public TrainingSession save(TrainingSession session) {
        TrainingSessionEntity entity = session.id() == null
                ? mapper.toEntity(session)
                : repository.findByIdAndOwnerId(session.id(), session.ownerId())
                    .orElseThrow(() -> new NotFoundException("TRAINING_SESSION_NOT_FOUND",
                            "No existe la sesión con id %d".formatted(session.id()),
                            Map.of("sessionId", session.id())));
        if (session.id() != null) {
            mapper.updateEntity(session, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(session.ownerId());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<TrainingSession> findById(Long id, Long ownerId) {
        return repository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<TrainingSession> findAll(Long ownerId, Pageable pageable) {
        return repository.findByOwnerId(ownerId, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("TRAINING_SESSION_NOT_FOUND",
                        "No existe la sesión con id %d".formatted(id), Map.of("sessionId", id)));
        entity.softDelete(Instant.now(), Duration.ofDays(30));
        repository.save(entity);
    }

    @Override
    @Transactional
    public TrainingSession restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE training_session SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now().toString());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("TRAINING_SESSION_NOT_FOUND",
                    "Sesión no encontrada en papelera", Map.of("sessionId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }

    @Override
    public Page<TrainingSession> findTrash(Long ownerId, Pageable pageable) {
        var countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM training_session WHERE owner_id = ?1 AND deleted_at IS NOT NULL");
        countQuery.setParameter(1, ownerId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        var listQuery = em.createNativeQuery(
                "SELECT * FROM training_session WHERE owner_id = ?1 AND deleted_at IS NOT NULL ORDER BY deleted_at DESC LIMIT ?2 OFFSET ?3",
                TrainingSessionEntity.class);
        listQuery.setParameter(1, ownerId);
        listQuery.setParameter(2, pageable.getPageSize());
        listQuery.setParameter(3, pageable.getOffset());
        @SuppressWarnings("unchecked")
        var list = (List<TrainingSessionEntity>) listQuery.getResultList();
        return new PageImpl<>(list.stream().map(mapper::toDomain).toList(), pageable, total);
    }

    @Override
    @Transactional
    public WorkedTechnique upsertWorkedTechnique(Long sessionId, Long ownerId, WorkedTechnique wt) {
        var session = repository.findByIdAndOwnerId(sessionId, ownerId)
                .orElseThrow(() -> new NotFoundException("TRAINING_SESSION_NOT_FOUND",
                        "No existe la sesión con id %d".formatted(sessionId),
                        Map.of("sessionId", sessionId)));

        var existingOpt = session.getWorkedTechniques().stream()
                .filter(e -> e.getId().getTechniqueId().equals(wt.techniqueId()))
                .findFirst();

        TrainingSessionTechniqueEntity entity = existingOpt.orElseGet(() -> {
            var e = new TrainingSessionTechniqueEntity();
            e.setId(new TrainingSessionTechniqueId(sessionId, wt.techniqueId()));
            e.setTrainingSession(session);
            session.getWorkedTechniques().add(e);
            return e;
        });
        entity.setRepCount(wt.repCount());
        entity.setNotesMarkdown(wt.notesMarkdown());

        repository.save(session);

        return WorkedTechnique.builder()
                .trainingSessionId(sessionId)
                .techniqueId(wt.techniqueId())
                .repCount(entity.getRepCount())
                .notesMarkdown(entity.getNotesMarkdown())
                .build();
    }

    @Override
    public long countByOwnerAndWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd) {
        return repository.countByOwnerIdAndSessionDateBetween(ownerId, weekStart, weekEnd);
    }

    @Override
    @Transactional
    public void removeWorkedTechnique(Long sessionId, Long ownerId, Long techniqueId) {
        var session = repository.findByIdAndOwnerId(sessionId, ownerId)
                .orElseThrow(() -> new NotFoundException("TRAINING_SESSION_NOT_FOUND",
                        "No existe la sesión con id %d".formatted(sessionId),
                        Map.of("sessionId", sessionId)));
        boolean removed = session.getWorkedTechniques()
                .removeIf(e -> e.getId().getTechniqueId().equals(techniqueId));
        if (!removed) {
            throw new NotFoundException("WORKED_TECHNIQUE_NOT_FOUND",
                    "Técnica %d no encontrada en sesión %d".formatted(techniqueId, sessionId),
                    Map.of("sessionId", sessionId, "techniqueId", techniqueId));
        }
        repository.save(session);
    }
}
