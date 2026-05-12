package com.ossflow.catalog.exercise.infrastructure.persistence;

import com.ossflow.catalog.exercise.application.port.ExerciseRepositoryPort;
import com.ossflow.catalog.exercise.domain.EquipmentType;
import com.ossflow.catalog.exercise.domain.Exercise;
import com.ossflow.catalog.exercise.domain.ExerciseCategory;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.shared.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExercisePersistenceAdapter implements ExerciseRepositoryPort {

    private final ExerciseJpaRepository repository;
    private final ExercisePersistenceMapper mapper;
    private final EntityManager em;

    @Override
    public Exercise save(Exercise exercise) {
        ExerciseEntity entity = exercise.id() == null
                ? mapper.toEntity(exercise)
                : repository.findByIdAndOwnerId(exercise.id(), exercise.ownerId())
                    .orElseThrow(() -> new NotFoundException("EXERCISE_NOT_FOUND",
                            "No existe el ejercicio con id %d".formatted(exercise.id()),
                            Map.of("exerciseId", exercise.id())));
        if (exercise.id() != null) {
            mapper.updateEntity(exercise, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(exercise.ownerId());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Exercise> findById(Long id, Long ownerId) {
        return repository.findByIdReadable(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<Exercise> findAll(Long ownerId, ExerciseCategory category, EquipmentType equipment,
                                   Visibility visibility, Pageable pageable) {
        return repository.findByFilters(ownerId, category, equipment, visibility, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(Long ownerId, String name) {
        return repository.existsByOwnerIdAndName(ownerId, name);
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("EXERCISE_NOT_FOUND",
                        "No existe el ejercicio con id %d".formatted(id), Map.of("exerciseId", id)));
        entity.softDelete(Instant.now(), Duration.ofDays(30));
        repository.save(entity);
    }

    @Override
    @Transactional
    public Exercise restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE exercise SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now().toString());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("EXERCISE_NOT_FOUND",
                    "Ejercicio no encontrado en papelera", Map.of("exerciseId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }
}
