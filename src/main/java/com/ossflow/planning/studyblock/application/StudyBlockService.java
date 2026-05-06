package com.ossflow.planning.studyblock.application;

import com.ossflow.planning.studyblock.application.port.StudyBlockRepositoryPort;
import com.ossflow.planning.studyblock.domain.StudyBlock;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyBlockService {

    private final StudyBlockRepositoryPort repository;

    public StudyBlock create(StudyBlock studyBlock) {
        StudyBlock saved = repository.save(studyBlock);
        log.info("StudyBlock creado id={} title={}", saved.id(), saved.title());
        return saved;
    }

    public StudyBlock findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("STUDY_BLOCK_NOT_FOUND",
                        "No existe el bloque de estudio con id %d".formatted(id),
                        Map.of("studyBlockId", id)));
    }

    public List<StudyBlock> listByPlan(Long studyPlanId) {
        return repository.findByStudyPlanId(studyPlanId);
    }

    public StudyBlock update(Long id, StudyBlock updated) {
        StudyBlock existing = findById(id);
        StudyBlock toSave = updated.toBuilder()
                .id(existing.id())
                .studyPlanId(existing.studyPlanId())
                .createdAt(existing.createdAt())
                .build();
        return repository.save(toSave);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
        log.info("StudyBlock eliminado id={}", id);
    }
}
