package com.ossflow.coaching.note.application.port;

import com.ossflow.coaching.note.domain.AthleteNote;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AthleteNoteRepositoryPort {
    AthleteNote save(AthleteNote note);
    List<AthleteNote> findByCoachIdAndAthleteIdOrderByCreatedAtDesc(Long coachId, Long athleteId);
    Optional<AthleteNote> findByIdAndAthleteId(Long id, Long athleteId);
    List<AthleteNote> findReceivedByAthleteId(Long athleteId);
    long countUnreadByAthleteId(Long athleteId);
    int softDeleteByIdAndCoachId(Long id, Long coachId);
    int markRead(Long id, Long athleteId);
    int purgeOlderThan(Instant threshold);
}
