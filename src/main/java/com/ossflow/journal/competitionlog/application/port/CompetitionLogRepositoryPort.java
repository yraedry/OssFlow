package com.ossflow.journal.competitionlog.application.port;

import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CompetitionLogRepositoryPort {
    CompetitionLog save(CompetitionLog log);
    Optional<CompetitionLog> findById(Long id, Long ownerId);
    Page<CompetitionLog> findAll(Long ownerId, Pageable pageable);
    void softDelete(Long id, Long ownerId);
    CompetitionLog restore(Long id, Long ownerId);
    Page<CompetitionLog> findTrash(Long ownerId, Pageable pageable);
}
