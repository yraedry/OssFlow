package com.ossflow.planning.studyplan.application.port;

import com.ossflow.planning.studyplan.domain.StudyPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StudyPlanRepositoryPort {
    StudyPlan save(StudyPlan studyPlan);
    Optional<StudyPlan> findById(Long id, Long ownerId);
    Page<StudyPlan> findAll(Long ownerId, Pageable pageable);
    boolean existsByOwnerIdAndTitle(Long ownerId, String title);
    void softDelete(Long id, Long ownerId);
    StudyPlan restore(Long id, Long ownerId);
    Page<StudyPlan> findTrash(Long ownerId, Pageable pageable);
}
