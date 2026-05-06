package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.shared.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PositionPersistenceAdapter implements PositionRepositoryPort {

    private final PositionJpaRepository repository;
    private final PositionPersistenceMapper mapper;
    private final EntityManager em;

    @Override
    public Position save(Position position) {
        PositionEntity entity = position.id() == null
                ? mapper.toEntity(position)
                : repository.findByIdAndOwnerId(position.id(), position.ownerId())
                    .orElseThrow(() -> new NotFoundException("POSITION_NOT_FOUND",
                            "No existe la posición con id %d".formatted(position.id()),
                            Map.of("positionId", position.id())));
        if (position.id() != null) {
            mapper.updateEntity(position, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(position.ownerId());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Position> findById(Long id, Long ownerId) {
        return repository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<Position> findAll(Long ownerId, String nameFilter, Pageable pageable) {
        Page<PositionEntity> page = (nameFilter == null || nameFilter.isBlank())
                ? repository.findByOwnerId(ownerId, pageable)
                : repository.findByOwnerIdAndNameContainingIgnoreCase(ownerId, nameFilter, pageable);
        return page.map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(Long ownerId, String name) {
        return repository.existsByOwnerIdAndName(ownerId, name);
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("POSITION_NOT_FOUND",
                        "No existe la posición con id %d".formatted(id), Map.of("positionId", id)));
        entity.softDelete(Instant.now(), java.time.Duration.ofDays(30));
        repository.save(entity);
    }

    @Override
    public Optional<Position> findInTrashById(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "SELECT * FROM position WHERE id = ?1 AND owner_id = ?2 AND deleted_at IS NOT NULL",
                PositionEntity.class);
        query.setParameter(1, id);
        query.setParameter(2, ownerId);
        @SuppressWarnings("unchecked")
        var list = (List<PositionEntity>) query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain(list.get(0)));
    }

    @Override
    @Transactional
    public Position restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE position SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now().toString());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("POSITION_NOT_FOUND",
                    "Posición no encontrada en papelera", Map.of("positionId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }

    @Override
    public Page<Position> findTrash(Long ownerId, Pageable pageable) {
        var countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM position WHERE owner_id = ?1 AND deleted_at IS NOT NULL");
        countQuery.setParameter(1, ownerId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        var listQuery = em.createNativeQuery(
                "SELECT * FROM position WHERE owner_id = ?1 AND deleted_at IS NOT NULL ORDER BY deleted_at DESC LIMIT ?2 OFFSET ?3",
                PositionEntity.class);
        listQuery.setParameter(1, ownerId);
        listQuery.setParameter(2, pageable.getPageSize());
        listQuery.setParameter(3, pageable.getOffset());
        @SuppressWarnings("unchecked")
        var list = (List<PositionEntity>) listQuery.getResultList();
        return new PageImpl<>(list.stream().map(mapper::toDomain).toList(), pageable, total);
    }
}
