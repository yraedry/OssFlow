package com.ossflow.journal.physicalsession.infrastructure.persistence;

import com.ossflow.journal.physicalsession.application.port.PhysicalSessionRepositoryPort;
import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PhysicalSessionPersistenceAdapter implements PhysicalSessionRepositoryPort {

    private final PhysicalSessionJpaRepository jpaRepository;
    private final PhysicalSessionPersistenceMapper mapper;

    @Override
    public PhysicalSession save(PhysicalSession session) {
        PhysicalSessionEntity entity = mapper.toEntity(session);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PhysicalSession> findById(Long id, Long ownerId) {
        return jpaRepository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<PhysicalSession> findAll(Long ownerId, Pageable pageable) {
        return jpaRepository.findByOwnerId(ownerId, pageable).map(mapper::toDomain);
    }

    @Override
    public void softDelete(Long id, Long ownerId) {
        PhysicalSessionEntity entity = jpaRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException(
                        "PHYSICAL_SESSION_NOT_FOUND",
                        "No existe la sesión física con id %d".formatted(id),
                        Map.of("sessionId", id)));
        entity.softDelete(Instant.now(), Duration.ofDays(30));
        jpaRepository.save(entity);
    }

    @Override
    public long countByOwnerAndWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd) {
        return jpaRepository.countByOwnerIdAndSessionDateBetween(ownerId, weekStart, weekEnd);
    }
}
