package com.ossflow.planning.studyitem.application;

import com.ossflow.planning.studyitem.application.port.StudyItemRepositoryPort;
import com.ossflow.planning.studyitem.domain.StudyItem;
import com.ossflow.planning.studyitem.domain.StudyItemStatus;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyItemService {

    private final StudyItemRepositoryPort repository;
    private final StudyItemStateMachine stateMachine;
    private final ApplicationEventPublisher events;

    public StudyItem create(StudyItem studyItem) {
        StudyItem saved = repository.save(studyItem);
        log.info("StudyItem creado id={}", saved.id());
        return saved;
    }

    public StudyItem findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("STUDY_ITEM_NOT_FOUND",
                        "No existe el item de estudio con id %d".formatted(id),
                        Map.of("studyItemId", id)));
    }

    public List<StudyItem> listByBlock(Long blockId) {
        return repository.findByBlockId(blockId);
    }

    public StudyItem update(Long id, StudyItem updated) {
        StudyItem existing = findById(id);
        StudyItem toSave = updated.toBuilder()
                .id(existing.id())
                .studyBlockId(existing.studyBlockId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build();
        return repository.save(toSave);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
        log.info("StudyItem eliminado id={}", id);
    }

    public StudyItem transition(Long itemId, StudyItemStatus targetStatus) {
        var item = findById(itemId);
        stateMachine.assertTransition(item.status(), targetStatus);
        var updated = repository.save(item.toBuilder()
                .status(targetStatus)
                .completedAt(targetStatus == StudyItemStatus.DONE ? Instant.now() : null)
                .build());
        events.publishEvent(new StudyItemStatusChangedEvent(
                updated.id(), null, item.status(), targetStatus, Instant.now()));
        return updated;
    }
}
