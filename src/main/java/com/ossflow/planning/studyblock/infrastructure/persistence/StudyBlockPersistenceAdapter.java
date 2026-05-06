package com.ossflow.planning.studyblock.infrastructure.persistence;

import com.ossflow.planning.studyblock.application.port.StudyBlockRepositoryPort;
import com.ossflow.planning.studyblock.domain.StudyBlock;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudyBlockPersistenceAdapter implements StudyBlockRepositoryPort {

    private final StudyBlockJpaRepository repository;
    private final StudyBlockPersistenceMapper mapper;

    @Override
    public StudyBlock save(StudyBlock studyBlock) {
        StudyBlockEntity entity = studyBlock.id() == null
                ? mapper.toEntity(studyBlock)
                : repository.findById(studyBlock.id())
                    .orElseThrow(() -> new NotFoundException("STUDY_BLOCK_NOT_FOUND",
                            "No existe el bloque de estudio con id %d".formatted(studyBlock.id()),
                            Map.of("studyBlockId", studyBlock.id())));
        if (studyBlock.id() != null) {
            mapper.updateEntity(studyBlock, entity);
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<StudyBlock> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StudyBlock> findByStudyPlanId(Long studyPlanId) {
        return repository.findByStudyPlanId(studyPlanId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
