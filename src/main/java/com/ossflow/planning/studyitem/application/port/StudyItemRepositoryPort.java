package com.ossflow.planning.studyitem.application.port;

import com.ossflow.planning.studyitem.domain.StudyItem;

import java.util.List;
import java.util.Optional;

public interface StudyItemRepositoryPort {
    StudyItem save(StudyItem studyItem);
    Optional<StudyItem> findById(Long id);
    List<StudyItem> findByBlockId(Long blockId);
    void deleteById(Long id);
}
