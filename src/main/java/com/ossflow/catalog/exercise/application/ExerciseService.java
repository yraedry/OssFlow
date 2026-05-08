package com.ossflow.catalog.exercise.application;

import com.ossflow.catalog.exercise.application.port.ExerciseRepositoryPort;
import com.ossflow.catalog.exercise.domain.EquipmentType;
import com.ossflow.catalog.exercise.domain.Exercise;
import com.ossflow.catalog.exercise.domain.ExerciseCategory;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.shared.exception.DuplicateNameException;
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
public class ExerciseService {

    private final ExerciseRepositoryPort repository;

    public Exercise create(Exercise exercise) {
        if (repository.existsByName(exercise.ownerId(), exercise.name())) {
            throw new DuplicateNameException("EXERCISE_NAME_DUPLICATE",
                    "Ya existe un ejercicio con el nombre '%s'".formatted(exercise.name()),
                    Map.of("name", exercise.name()));
        }
        Exercise saved = repository.save(exercise);
        log.info("Ejercicio creado id={} name={}", saved.id(), saved.name());
        return saved;
    }

    public Exercise findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("EXERCISE_NOT_FOUND",
                        "No existe el ejercicio con id %d".formatted(id),
                        Map.of("exerciseId", id)));
    }

    public Page<Exercise> list(Long ownerId, ExerciseCategory category, EquipmentType equipment,
                                Visibility visibility, Pageable pageable) {
        return repository.findAll(ownerId, category, equipment, visibility, pageable);
    }

    public Exercise replace(Long id, Long ownerId, Exercise replacement) {
        Exercise existing = findById(id, ownerId);
        if (!existing.name().equals(replacement.name())
                && repository.existsByName(ownerId, replacement.name())) {
            throw new DuplicateNameException("EXERCISE_NAME_DUPLICATE",
                    "Ya existe un ejercicio con el nombre '%s'".formatted(replacement.name()),
                    Map.of("name", replacement.name()));
        }
        return repository.save(replacement.toBuilder()
                .id(existing.id()).ownerId(existing.ownerId())
                .createdAt(existing.createdAt()).version(existing.version()).build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("Ejercicio soft-deleted id={}", id);
    }

    public Exercise restore(Long id, Long ownerId) {
        Exercise restored = repository.restore(id, ownerId);
        log.info("Ejercicio restaurado id={}", id);
        return restored;
    }
}
