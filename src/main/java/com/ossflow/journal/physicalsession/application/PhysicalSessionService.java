package com.ossflow.journal.physicalsession.application;

import com.ossflow.journal.physicalsession.application.port.PhysicalSessionRepositoryPort;
import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhysicalSessionService {

    private final PhysicalSessionRepositoryPort repository;

    public PhysicalSession create(PhysicalSession session) {
        PhysicalSession saved = repository.save(session);
        log.info("PhysicalSession creada id={}", saved.id());
        return saved;
    }

    public PhysicalSession findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException(
                        "PHYSICAL_SESSION_NOT_FOUND",
                        "No existe la sesión física con id %d".formatted(id),
                        Map.of("sessionId", id)));
    }

    public Page<PhysicalSession> list(Long ownerId, Pageable pageable) {
        return repository.findAll(ownerId, pageable);
    }

    public PhysicalSession replace(Long id, Long ownerId, PhysicalSession replacement) {
        PhysicalSession existing = findById(id, ownerId);
        return repository.save(replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("PhysicalSession soft-deleted id={}", id);
    }

    public long countByWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd) {
        return repository.countByOwnerAndWeek(ownerId, weekStart, weekEnd);
    }
}
