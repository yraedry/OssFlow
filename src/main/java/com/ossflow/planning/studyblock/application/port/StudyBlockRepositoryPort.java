package com.ossflow.planning.studyblock.application.port;

import com.ossflow.planning.studyblock.domain.StudyBlock;

import java.util.List;
import java.util.Optional;

public interface StudyBlockRepositoryPort {
    StudyBlock save(StudyBlock studyBlock);
    Optional<StudyBlock> findById(Long id);
    List<StudyBlock> findByStudyPlanId(Long studyPlanId);
    void deleteById(Long id);
}
