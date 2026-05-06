package com.ossflow.planning.studyitem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyItemJpaRepository extends JpaRepository<StudyItemEntity, Long> {

    List<StudyItemEntity> findByStudyBlockId(Long studyBlockId);
}
