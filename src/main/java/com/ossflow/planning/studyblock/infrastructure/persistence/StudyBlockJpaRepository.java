package com.ossflow.planning.studyblock.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyBlockJpaRepository extends JpaRepository<StudyBlockEntity, Long> {

    List<StudyBlockEntity> findByStudyPlanId(Long studyPlanId);
}
