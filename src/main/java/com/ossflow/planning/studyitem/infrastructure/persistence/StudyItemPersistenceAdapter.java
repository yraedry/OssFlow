package com.ossflow.planning.studyitem.infrastructure.persistence;

import com.ossflow.planning.studyitem.application.port.StudyItemRepositoryPort;
import com.ossflow.planning.studyitem.domain.StudyItem;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudyItemPersistenceAdapter implements StudyItemRepositoryPort {

    private final StudyItemJpaRepository repository;
    private final StudyItemPersistenceMapper mapper;

    @Override
    public StudyItem save(StudyItem studyItem) {
        StudyItemEntity entity = studyItem.id() == null
                ? mapper.toEntity(studyItem)
                : repository.findById(studyItem.id())
                    .orElseThrow(() -> new NotFoundException("STUDY_ITEM_NOT_FOUND",
                            "No existe el item de estudio con id %d".formatted(studyItem.id()),
                            Map.of("studyItemId", studyItem.id())));
        if (studyItem.id() != null) {
            mapper.updateEntity(studyItem, entity);
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<StudyItem> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StudyItem> findByBlockId(Long blockId) {
        return repository.findByStudyBlockId(blockId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
