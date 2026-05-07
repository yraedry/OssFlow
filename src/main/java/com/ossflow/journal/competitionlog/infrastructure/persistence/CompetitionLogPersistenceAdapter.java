package com.ossflow.journal.competitionlog.infrastructure.persistence;

import com.ossflow.journal.competitionlog.application.port.CompetitionLogRepositoryPort;
import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import com.ossflow.journal.competitionlog.domain.CompetitionMatch;
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
public class CompetitionLogPersistenceAdapter implements CompetitionLogRepositoryPort {

    private final CompetitionLogJpaRepository repository;
    private final CompetitionLogPersistenceMapper mapper;
    private final CompetitionMatchMapper matchMapper;
    private final EntityManager em;

    @Override
    @Transactional
    public CompetitionLog save(CompetitionLog log) {
        CompetitionLogEntity entity = log.id() == null
                ? mapper.toEntity(log)
                : repository.findByIdAndOwnerId(log.id(), log.ownerId())
                    .orElseThrow(() -> new NotFoundException("COMPETITION_LOG_NOT_FOUND",
                            "No existe el registro de competición con id %d".formatted(log.id()),
                            Map.of("competitionLogId", log.id())));
        if (log.id() != null) {
            mapper.updateEntity(log, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(log.ownerId());

        // First save to get the id for new entities
        CompetitionLogEntity savedEntity = repository.save(entity);

        // Sync matches if provided
        if (log.matches() != null) {
            savedEntity.getMatches().clear();
            for (CompetitionMatch match : log.matches()) {
                CompetitionMatchEntity matchEntity = matchMapper.toEntity(match);
                matchEntity.setCompetitionLog(savedEntity);
                savedEntity.getMatches().add(matchEntity);
            }
            savedEntity = repository.save(savedEntity);
        }

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompetitionLog> findById(Long id, Long ownerId) {
        return repository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompetitionLog> findAll(Long ownerId, Pageable pageable) {
        Page<CompetitionLogEntity> page = repository.findByOwnerId(ownerId, pageable);
        List<CompetitionLog> domain = page.getContent().stream().map(mapper::toDomain).toList();
        return new PageImpl<>(domain, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("COMPETITION_LOG_NOT_FOUND",
                        "No existe el registro de competición con id %d".formatted(id),
                        Map.of("competitionLogId", id)));
        entity.softDelete(Instant.now(), Duration.ofDays(30));
        repository.save(entity);
    }

    @Override
    @Transactional
    public CompetitionLog restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE competition_log SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now().toString());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("COMPETITION_LOG_NOT_FOUND",
                    "Registro de competición no encontrado en papelera",
                    Map.of("competitionLogId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }

    @Override
    public Page<CompetitionLog> findTrash(Long ownerId, Pageable pageable) {
        var countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM competition_log WHERE owner_id = ?1 AND deleted_at IS NOT NULL");
        countQuery.setParameter(1, ownerId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        var listQuery = em.createNativeQuery(
                "SELECT * FROM competition_log WHERE owner_id = ?1 AND deleted_at IS NOT NULL ORDER BY deleted_at DESC LIMIT ?2 OFFSET ?3",
                CompetitionLogEntity.class);
        listQuery.setParameter(1, ownerId);
        listQuery.setParameter(2, pageable.getPageSize());
        listQuery.setParameter(3, pageable.getOffset());
        @SuppressWarnings("unchecked")
        var list = (List<CompetitionLogEntity>) listQuery.getResultList();
        return new PageImpl<>(list.stream().map(mapper::toDomain).toList(), pageable, total);
    }
}
