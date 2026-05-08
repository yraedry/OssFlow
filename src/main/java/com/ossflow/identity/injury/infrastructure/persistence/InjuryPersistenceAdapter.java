package com.ossflow.identity.injury.infrastructure.persistence;

import com.ossflow.identity.injury.application.port.InjuryRepositoryPort;
import com.ossflow.identity.injury.domain.Injury;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InjuryPersistenceAdapter implements InjuryRepositoryPort {

    private final InjuryJpaRepository repository;
    private final InjuryPersistenceMapper mapper;

    @Override
    public Injury save(Injury injury) {
        InjuryEntity entity = injury.id() == null
                ? mapper.toEntity(injury)
                : repository.findByIdAndOwnerId(injury.id(), injury.ownerId())
                    .orElseThrow(() -> new NotFoundException("INJURY_NOT_FOUND",
                            "No existe la lesión con id %d".formatted(injury.id()),
                            Map.of("injuryId", injury.id())));
        if (injury.id() != null) {
            mapper.updateEntity(injury, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(injury.ownerId());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Injury> findById(Long id, Long ownerId) {
        return repository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public List<Injury> findAllByOwnerId(Long ownerId) {
        return repository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByIdAndOwnerId(Long id, Long ownerId) {
        repository.deleteByIdAndOwnerId(id, ownerId);
    }
}
