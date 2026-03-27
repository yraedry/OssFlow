package com.ossflow.technique.infra.adapter.out.db;

import com.ossflow.technique.application.port.out.TechniqueRepositoryPort;
import com.ossflow.technique.domain.model.Technique;
import com.ossflow.technique.infra.adapter.out.db.entity.TechniqueEntity;
import com.ossflow.technique.infra.adapter.out.db.mapper.TechniqueMapper;
import com.ossflow.technique.infra.adapter.out.db.repository.TechniqueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TechniqueDatabaseAdapter implements TechniqueRepositoryPort {

    private final TechniqueJpaRepository jpaRepository;
    private final TechniqueMapper mapper;

    @Override
    public Technique save(Technique technique) {
        TechniqueEntity entity = mapper.toEntity(technique);
        TechniqueEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Technique> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}