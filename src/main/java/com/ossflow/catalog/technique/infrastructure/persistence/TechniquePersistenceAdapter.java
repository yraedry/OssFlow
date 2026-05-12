package com.ossflow.catalog.technique.infrastructure.persistence;

import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
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
public class TechniquePersistenceAdapter implements TechniqueRepositoryPort {

    private final TechniqueJpaRepository repository;
    private final TechniquePersistenceMapper mapper;
    private final EntityManager em;

    @Override
    public Technique save(Technique technique) {
        TechniqueEntity entity = technique.id() == null
                ? mapper.toEntity(technique)
                : repository.findByIdAndOwnerId(technique.id(), technique.ownerId())
                    .orElseThrow(() -> new NotFoundException("TECHNIQUE_NOT_FOUND",
                            "No existe la técnica con id %d".formatted(technique.id()),
                            Map.of("techniqueId", technique.id())));
        if (technique.id() != null) {
            mapper.updateEntity(technique, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(technique.ownerId());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Technique> findById(Long id, Long ownerId) {
        return repository.findByIdReadable(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<Technique> findAll(Long ownerId, TechniqueCategory category, Belt belt,
                                    Modality modality, Long startPositionId, Long endPositionId,
                                    String search, Pageable pageable) {
        String categoryStr = category != null ? category.name() : null;
        String beltStr = belt != null ? belt.name() : null;
        String modalityStr = modality != null ? modality.name() : null;
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        return repository.findByFilters(ownerId, categoryStr, beltStr, modalityStr,
                startPositionId, endPositionId, searchParam, pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(Long ownerId, String name) {
        return repository.existsByOwnerIdAndName(ownerId, name);
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("TECHNIQUE_NOT_FOUND",
                        "No existe la técnica con id %d".formatted(id), Map.of("techniqueId", id)));
        entity.softDelete(Instant.now(), Duration.ofDays(30));
        repository.save(entity);
    }

    @Override
    public Optional<Technique> findInTrashById(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "SELECT * FROM technique WHERE id = ?1 AND owner_id = ?2 AND deleted_at IS NOT NULL",
                TechniqueEntity.class);
        query.setParameter(1, id);
        query.setParameter(2, ownerId);
        @SuppressWarnings("unchecked")
        var list = (List<TechniqueEntity>) query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain(list.get(0)));
    }

    @Override
    @Transactional
    public Technique restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE technique SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now().toString());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("TECHNIQUE_NOT_FOUND",
                    "Técnica no encontrada en papelera", Map.of("techniqueId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }

    @Override
    public Page<Technique> findTrash(Long ownerId, Pageable pageable) {
        var countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM technique WHERE owner_id = ?1 AND deleted_at IS NOT NULL");
        countQuery.setParameter(1, ownerId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        var listQuery = em.createNativeQuery(
                "SELECT * FROM technique WHERE owner_id = ?1 AND deleted_at IS NOT NULL ORDER BY deleted_at DESC LIMIT ?2 OFFSET ?3",
                TechniqueEntity.class);
        listQuery.setParameter(1, ownerId);
        listQuery.setParameter(2, pageable.getPageSize());
        listQuery.setParameter(3, pageable.getOffset());
        @SuppressWarnings("unchecked")
        var list = (List<TechniqueEntity>) listQuery.getResultList();
        return new PageImpl<>(list.stream().map(mapper::toDomain).toList(), pageable, total);
    }

    @Override
    public long countByPositionId(Long positionId) {
        return repository.countByStartPositionIdOrEndPositionId(positionId, positionId);
    }
}
