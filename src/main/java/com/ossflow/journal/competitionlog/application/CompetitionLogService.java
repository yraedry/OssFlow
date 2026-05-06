package com.ossflow.journal.competitionlog.application;

import com.ossflow.journal.competitionlog.application.port.CompetitionLogRepositoryPort;
import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import com.ossflow.journal.competitionlog.domain.CompetitionMatch;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitionLogService {

    private final CompetitionLogRepositoryPort repository;

    public CompetitionLog create(CompetitionLog competitionLog) {
        CompetitionLog saved = repository.save(competitionLog);
        log.info("CompetitionLog creado id={}", saved.id());
        return saved;
    }

    public CompetitionLog findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("COMPETITION_LOG_NOT_FOUND",
                        "No existe el registro de competición con id %d".formatted(id),
                        Map.of("competitionLogId", id)));
    }

    public Page<CompetitionLog> list(Long ownerId, Pageable pageable) {
        return repository.findAll(ownerId, pageable);
    }

    public CompetitionLog replace(Long id, Long ownerId, CompetitionLog replacement) {
        CompetitionLog existing = findById(id, ownerId);
        CompetitionLog toSave = replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build();
        return repository.save(toSave);
    }

    public CompetitionLog patch(Long id, Long ownerId, CompetitionLog patched) {
        return repository.save(patched.toBuilder()
                .id(id)
                .ownerId(ownerId)
                .build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("CompetitionLog soft-deleted id={}", id);
    }

    public CompetitionLog restore(Long id, Long ownerId) {
        CompetitionLog restored = repository.restore(id, ownerId);
        log.info("CompetitionLog restaurado id={}", restored.id());
        return restored;
    }

    public Page<CompetitionLog> trash(Long ownerId, Pageable pageable) {
        return repository.findTrash(ownerId, pageable);
    }

    public List<CompetitionMatch> getMatches(Long id, Long ownerId) {
        return findById(id, ownerId).matches();
    }

    public CompetitionLog addMatch(Long id, Long ownerId, CompetitionMatch match) {
        CompetitionLog existing = findById(id, ownerId);
        List<CompetitionMatch> matches = new ArrayList<>(existing.matches() != null ? existing.matches() : List.of());
        matches.add(match.toBuilder().competitionLogId(id).build());
        return repository.save(existing.toBuilder().matches(matches).build());
    }

    public CompetitionLog removeMatch(Long id, Long ownerId, Long matchId) {
        CompetitionLog existing = findById(id, ownerId);
        List<CompetitionMatch> matches = existing.matches() != null
                ? existing.matches().stream().filter(m -> !m.id().equals(matchId)).toList()
                : List.of();
        return repository.save(existing.toBuilder().matches(matches).build());
    }
}
