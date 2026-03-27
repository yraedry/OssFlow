package com.ossflow.technique.infra.adapter.out.db;

import com.ossflow.technique.application.port.out.PositionRepositoryPort;
import com.ossflow.technique.domain.model.Position;
import com.ossflow.technique.infra.adapter.out.db.entity.PositionEntity;
import com.ossflow.technique.infra.adapter.out.db.mapper.PositionMapper;
import com.ossflow.technique.infra.adapter.out.db.repository.PositionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PositionDatabaseAdapter implements PositionRepositoryPort {

    private final PositionJpaRepository repository;
    private final PositionMapper mapper;

    @Override
    public Position save(Position position) {
        PositionEntity entity = mapper.toEntity(position);
        PositionEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Position> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Position> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Position> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(mapper::toDomain)
                .toList();
    }
}