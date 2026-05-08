package com.ossflow.catalog.exercise.application.port;

import com.ossflow.catalog.exercise.domain.EquipmentType;
import com.ossflow.catalog.exercise.domain.Exercise;
import com.ossflow.catalog.exercise.domain.ExerciseCategory;
import com.ossflow.catalog.position.domain.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ExerciseRepositoryPort {
    Exercise save(Exercise exercise);
    Optional<Exercise> findById(Long id, Long ownerId);
    Page<Exercise> findAll(Long ownerId, ExerciseCategory category, EquipmentType equipment,
                           Visibility visibility, Pageable pageable);
    boolean existsByName(Long ownerId, String name);
    void softDelete(Long id, Long ownerId);
    Exercise restore(Long id, Long ownerId);
}
