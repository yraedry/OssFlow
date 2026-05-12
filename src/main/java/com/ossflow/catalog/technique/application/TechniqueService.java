package com.ossflow.catalog.technique.application;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
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
public class TechniqueService {

    private final TechniqueRepositoryPort repository;
    private final PositionRepositoryPort positionRepository;

    public Technique create(Technique technique) {
        validatePosition(technique.startPositionId(), technique.ownerId(), "startPositionId");
        if (technique.endPositionId() != null) {
            validatePosition(technique.endPositionId(), technique.ownerId(), "endPositionId");
        }
        if (repository.existsByName(technique.ownerId(), technique.name())) {
            throw new DuplicateNameException("TECHNIQUE_NAME_DUPLICATE",
                    "Ya existe una técnica con el nombre '%s'".formatted(technique.name()),
                    Map.of("name", technique.name()));
        }
        Technique saved = repository.save(technique);
        log.info("Técnica creada id={} name={}", saved.id(), saved.name());
        return saved;
    }

    public Technique findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("TECHNIQUE_NOT_FOUND",
                        "No existe la técnica con id %d".formatted(id),
                        Map.of("techniqueId", id)));
    }

    public Page<Technique> list(Long ownerId, TechniqueCategory category, Belt belt,
                                 Modality modality, Long startPositionId, Long endPositionId,
                                 String search, Pageable pageable) {
        return repository.findAll(ownerId, category, belt, modality, startPositionId, endPositionId, search, pageable);
    }

    public Technique replace(Long id, Long ownerId, Technique replacement) {
        Technique existing = findById(id, ownerId);
        if (!existing.name().equals(replacement.name())
                && repository.existsByName(ownerId, replacement.name())) {
            throw new DuplicateNameException("TECHNIQUE_NAME_DUPLICATE",
                    "Ya existe una técnica con el nombre '%s'".formatted(replacement.name()),
                    Map.of("name", replacement.name()));
        }
        return repository.save(replacement.toBuilder()
                .id(existing.id()).ownerId(existing.ownerId())
                .createdAt(existing.createdAt()).version(existing.version()).build());
    }

    public Technique patch(Long id, Long ownerId, Technique patched) {
        return repository.save(patched.toBuilder().id(id).ownerId(ownerId).build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("Técnica soft-deleted id={}", id);
    }

    public Technique restore(Long id, Long ownerId) {
        Technique restored = repository.restore(id, ownerId);
        log.info("Técnica restaurada id={}", id);
        return restored;
    }

    public Page<Technique> trash(Long ownerId, Pageable pageable) {
        return repository.findTrash(ownerId, pageable);
    }

    private void validatePosition(Long positionId, Long ownerId, String field) {
        if (positionRepository.findById(positionId, ownerId).isEmpty()) {
            throw new NotFoundException("POSITION_NOT_FOUND",
                    "No existe la posición con id %d".formatted(positionId),
                    Map.of("positionId", positionId, "field", field));
        }
    }
}
