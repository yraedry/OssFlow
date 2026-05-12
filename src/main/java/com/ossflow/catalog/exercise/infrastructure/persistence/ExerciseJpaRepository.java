package com.ossflow.catalog.exercise.infrastructure.persistence;

import com.ossflow.catalog.exercise.domain.EquipmentType;
import com.ossflow.catalog.exercise.domain.ExerciseCategory;
import com.ossflow.catalog.position.domain.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseJpaRepository extends JpaRepository<ExerciseEntity, Long> {

    Optional<ExerciseEntity> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT e FROM ExerciseEntity e WHERE e.id = :id AND (e.ownerId = :ownerId OR e.visibility = com.ossflow.catalog.position.domain.Visibility.PUBLIC)")
    Optional<ExerciseEntity> findByIdReadable(Long id, Long ownerId);

    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query("""
            SELECT e FROM ExerciseEntity e
            WHERE (e.ownerId = :ownerId OR e.visibility = com.ossflow.catalog.position.domain.Visibility.PUBLIC)
            AND (:category IS NULL OR e.category = :category)
            AND (:equipment IS NULL OR e.equipment = :equipment)
            AND (:visibility IS NULL OR e.visibility = :visibility)
            """)
    Page<ExerciseEntity> findByFilters(Long ownerId, ExerciseCategory category,
                                       EquipmentType equipment, Visibility visibility,
                                       Pageable pageable);
}
