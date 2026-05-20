package com.ossflow.coaching.note.infrastructure.persistence;

import com.ossflow.coaching.note.application.port.AthleteNoteRepositoryPort;
import com.ossflow.coaching.note.domain.AthleteNote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AthleteNotePersistenceAdapter implements AthleteNoteRepositoryPort {

    private final AthleteNoteJpaRepository jpa;
    private final AthleteNoteMapper mapper;

    @Override
    public AthleteNote save(AthleteNote note) {
        return mapper.toDomain(jpa.save(mapper.toEntity(note)));
    }

    @Override
    public List<AthleteNote> findByCoachIdAndAthleteIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long coachId, Long athleteId) {
        return jpa.findByCoachIdAndAthleteIdAndDeletedAtIsNullOrderByCreatedAtDesc(coachId, athleteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<AthleteNote> findByIdAndAthleteIdAndDeletedAtIsNull(Long id, Long athleteId) {
        return jpa.findByIdAndAthleteIdAndDeletedAtIsNull(id, athleteId).map(mapper::toDomain);
    }

    @Override
    public List<AthleteNote> findReceivedByAthleteId(Long athleteId) {
        return jpa.findReceivedByAthleteId(athleteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countUnreadByAthleteId(Long athleteId) {
        return jpa.countUnreadByAthleteId(athleteId);
    }

    @Override
    @Transactional
    public int softDeleteByIdAndCoachId(Long id, Long coachId) {
        return jpa.softDeleteByIdAndCoachId(id, coachId, Instant.now());
    }

    @Override
    @Transactional
    public int markRead(Long id, Long athleteId) {
        return jpa.markRead(id, athleteId, Instant.now());
    }

    @Override
    @Transactional
    public int purgeOlderThan(Instant threshold) {
        return jpa.purgeOlderThan(threshold);
    }
}
