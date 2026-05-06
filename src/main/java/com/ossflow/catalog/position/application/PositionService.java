package com.ossflow.catalog.position.application;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.position.domain.Position;
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
public class PositionService {

    private final PositionRepositoryPort repository;

    public Position create(Position position) {
        if (repository.existsByName(position.ownerId(), position.name())) {
            throw new DuplicateNameException("POSITION_NAME_DUPLICATE",
                    "Ya existe una posición con el nombre '%s'".formatted(position.name()),
                    Map.of("name", position.name()));
        }
        Position saved = repository.save(position);
        log.info("Posición creada id={} name={}", saved.id(), saved.name());
        return saved;
    }

    public Position findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("POSITION_NOT_FOUND",
                        "No existe la posición con id %d".formatted(id),
                        Map.of("positionId", id)));
    }

    public Page<Position> list(Long ownerId, String nameFilter, Pageable pageable) {
        return repository.findAll(ownerId, nameFilter, pageable);
    }

    public Position replace(Long id, Long ownerId, Position replacement) {
        Position existing = findById(id, ownerId);
        if (!existing.name().equals(replacement.name())
                && repository.existsByName(ownerId, replacement.name())) {
            throw new DuplicateNameException("POSITION_NAME_DUPLICATE",
                    "Ya existe una posición con el nombre '%s'".formatted(replacement.name()),
                    Map.of("name", replacement.name()));
        }
        Position toSave = replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build();
        return repository.save(toSave);
    }

    public Position patch(Long id, Long ownerId, Position patched) {
        return repository.save(patched.toBuilder()
                .id(id)
                .ownerId(ownerId)
                .build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("Posición soft-deleted id={}", id);
    }

    public Position restore(Long id, Long ownerId) {
        Position restored = repository.restore(id, ownerId);
        log.info("Posición restaurada id={}", id);
        return restored;
    }

    public Page<Position> trash(Long ownerId, Pageable pageable) {
        return repository.findTrash(ownerId, pageable);
    }
}
