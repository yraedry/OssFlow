package com.ossflow.planning.studyplan.infrastructure.persistence;

import com.ossflow.planning.studyplan.application.port.StudyPlanRepositoryPort;
import com.ossflow.planning.studyplan.domain.StudyPlan;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudyPlanPersistenceAdapter implements StudyPlanRepositoryPort {

    private final StudyPlanJpaRepository repository;
    private final StudyPlanPersistenceMapper mapper;
    private final EntityManager em;

    @Override
    public StudyPlan save(StudyPlan studyPlan) {
        StudyPlanEntity entity = studyPlan.id() == null
                ? mapper.toEntity(studyPlan)
                : repository.findByIdAndOwnerId(studyPlan.id(), studyPlan.ownerId())
                    .orElseThrow(() -> new NotFoundException("STUDY_PLAN_NOT_FOUND",
                            "No existe el plan de estudio con id %d".formatted(studyPlan.id()),
                            Map.of("studyPlanId", studyPlan.id())));
        if (studyPlan.id() != null) {
            mapper.updateEntity(studyPlan, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(studyPlan.ownerId());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<StudyPlan> findById(Long id, Long ownerId) {
        return repository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<StudyPlan> findAll(Long ownerId, Pageable pageable) {
        return repository.findByOwnerId(ownerId, pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOwnerIdAndTitle(Long ownerId, String title) {
        return repository.existsByOwnerIdAndTitle(ownerId, title);
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("STUDY_PLAN_NOT_FOUND",
                        "No existe el plan de estudio con id %d".formatted(id),
                        Map.of("studyPlanId", id)));
        entity.softDelete(Instant.now(), Duration.ofDays(30));
        repository.save(entity);
    }

    @Override
    @Transactional
    public StudyPlan restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE study_plan SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now().toString());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("STUDY_PLAN_NOT_FOUND",
                    "Plan de estudio no encontrado en papelera",
                    Map.of("studyPlanId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }

    @Override
    public Page<StudyPlan> findTrash(Long ownerId, Pageable pageable) {
        var countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM study_plan WHERE owner_id = ?1 AND deleted_at IS NOT NULL");
        countQuery.setParameter(1, ownerId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        var listQuery = em.createNativeQuery(
                "SELECT * FROM study_plan WHERE owner_id = ?1 AND deleted_at IS NOT NULL ORDER BY deleted_at DESC LIMIT ?2 OFFSET ?3",
                StudyPlanEntity.class);
        listQuery.setParameter(1, ownerId);
        listQuery.setParameter(2, pageable.getPageSize());
        listQuery.setParameter(3, pageable.getOffset());
        @SuppressWarnings("unchecked")
        var list = (List<StudyPlanEntity>) listQuery.getResultList();
        return new PageImpl<>(list.stream().map(mapper::toDomain).toList(), pageable, total);
    }
}
