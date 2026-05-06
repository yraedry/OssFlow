package com.ossflow.planning.studyplan.application;

import com.ossflow.planning.studyplan.application.port.StudyPlanRepositoryPort;
import com.ossflow.planning.studyplan.domain.StudyPlan;
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
public class StudyPlanService {

    private final StudyPlanRepositoryPort repository;

    public StudyPlan create(StudyPlan studyPlan) {
        if (repository.existsByOwnerIdAndTitle(studyPlan.ownerId(), studyPlan.title())) {
            throw new DuplicateNameException("STUDY_PLAN_TITLE_DUPLICATE",
                    "Ya existe un plan de estudio con el título '%s'".formatted(studyPlan.title()),
                    Map.of("title", studyPlan.title()));
        }
        StudyPlan saved = repository.save(studyPlan);
        log.info("StudyPlan creado id={} title={}", saved.id(), saved.title());
        return saved;
    }

    public StudyPlan findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("STUDY_PLAN_NOT_FOUND",
                        "No existe el plan de estudio con id %d".formatted(id),
                        Map.of("studyPlanId", id)));
    }

    public Page<StudyPlan> list(Long ownerId, Pageable pageable) {
        return repository.findAll(ownerId, pageable);
    }

    public StudyPlan replace(Long id, Long ownerId, StudyPlan replacement) {
        StudyPlan existing = findById(id, ownerId);
        if (!existing.title().equals(replacement.title())
                && repository.existsByOwnerIdAndTitle(ownerId, replacement.title())) {
            throw new DuplicateNameException("STUDY_PLAN_TITLE_DUPLICATE",
                    "Ya existe un plan de estudio con el título '%s'".formatted(replacement.title()),
                    Map.of("title", replacement.title()));
        }
        StudyPlan toSave = replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build();
        return repository.save(toSave);
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("StudyPlan soft-deleted id={}", id);
    }

    public StudyPlan restore(Long id, Long ownerId) {
        StudyPlan restored = repository.restore(id, ownerId);
        log.info("StudyPlan restaurado id={}", id);
        return restored;
    }

    public Page<StudyPlan> trash(Long ownerId, Pageable pageable) {
        return repository.findTrash(ownerId, pageable);
    }
}
