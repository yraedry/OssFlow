package com.ossflow.technique.application.port.out;

import com.ossflow.technique.domain.model.Position;

import java.util.List;
import java.util.Optional;

public interface PositionRepositoryPort {
    Position save(Position position);
    Optional<Position> findById(Long id);
    List<Position> findAll();
    List<Position> findByName(String name);
}