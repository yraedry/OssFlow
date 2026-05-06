package com.ossflow.planning.studyplan.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyPlanJpaRepository extends JpaRepository<StudyPlanEntity, Long> {

    Optional<StudyPlanEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<StudyPlanEntity> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByOwnerIdAndTitle(Long ownerId, String title);
}
