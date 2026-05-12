package com.ossflow.catalog.system.infrastructure.persistence;

import com.ossflow.catalog.system.application.port.SystemRepositoryPort;
import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.shared.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SystemPersistenceAdapter implements SystemRepositoryPort {

    private final SystemJpaRepository repository;
    private final SystemPersistenceMapper mapper;
    private final EntityManager em;

    @Override
    public OssSystem save(OssSystem system) {
        SystemEntity entity = system.id() == null
                ? mapper.toEntity(system)
                : repository.findByIdAndOwnerId(system.id(), system.ownerId())
                    .orElseThrow(() -> new NotFoundException("SYSTEM_NOT_FOUND",
                            "No existe el sistema con id %d".formatted(system.id()),
                            Map.of("systemId", system.id())));
        if (system.id() != null) mapper.updateEntity(system, entity);
        if (entity.getOwnerId() == null) entity.setOwnerId(system.ownerId());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<OssSystem> findById(Long id, Long ownerId) {
        return repository.findByIdReadable(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<OssSystem> findAll(Long ownerId, String search, Pageable pageable) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        return repository.findBySearch(ownerId, searchParam, pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(Long ownerId, String name) {
        return repository.existsByOwnerIdAndName(ownerId, name);
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("SYSTEM_NOT_FOUND",
                        "No existe el sistema con id %d".formatted(id), Map.of("systemId", id)));
        entity.softDelete(Instant.now(), Duration.ofDays(30));
        repository.save(entity);
    }

    @Override
    @Transactional
    public OssSystem restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE system SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now().toString());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("SYSTEM_NOT_FOUND",
                    "Sistema no encontrado en papelera", Map.of("systemId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }

    @Override
    public Page<OssSystem> findTrash(Long ownerId, Pageable pageable) {
        var countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM system WHERE owner_id = ?1 AND deleted_at IS NOT NULL");
        countQuery.setParameter(1, ownerId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        var listQuery = em.createNativeQuery(
                "SELECT * FROM system WHERE owner_id = ?1 AND deleted_at IS NOT NULL ORDER BY deleted_at DESC LIMIT ?2 OFFSET ?3",
                SystemEntity.class);
        listQuery.setParameter(1, ownerId);
        listQuery.setParameter(2, pageable.getPageSize());
        listQuery.setParameter(3, pageable.getOffset());
        @SuppressWarnings("unchecked")
        var list = (List<SystemEntity>) listQuery.getResultList();
        return new PageImpl<>(list.stream().map(mapper::toDomain).toList(), pageable, total);
    }
}
