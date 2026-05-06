package com.ossflow.catalog.position.application.port;

import com.ossflow.catalog.position.domain.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PositionRepositoryPort {
    Position save(Position position);
    Optional<Position> findById(Long id, Long ownerId);
    Page<Position> findAll(Long ownerId, String nameFilter, Pageable pageable);
    boolean existsByName(Long ownerId, String name);
    void softDelete(Long id, Long ownerId);
    Optional<Position> findInTrashById(Long id, Long ownerId);
    Position restore(Long id, Long ownerId);
    Page<Position> findTrash(Long ownerId, Pageable pageable);
}
