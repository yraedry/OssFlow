package com.ossflow.journal.physicalsession.application.port;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface PhysicalSessionRepositoryPort {
    PhysicalSession save(PhysicalSession session);
    Optional<PhysicalSession> findById(Long id, Long ownerId);
    Page<PhysicalSession> findAll(Long ownerId, Pageable pageable);
    void softDelete(Long id, Long ownerId);
    long countByOwnerAndWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd);
}
