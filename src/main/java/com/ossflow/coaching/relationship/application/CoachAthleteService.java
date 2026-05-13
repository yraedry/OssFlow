package com.ossflow.coaching.relationship.application;

import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachAthleteService {

    private final CoachAthleteRepositoryPort repo;

    public CoachAthleteRelationship link(Long coachId, Long athleteId, Long invitationId) {
        if (repo.findByCoachIdAndAthleteId(coachId, athleteId).isPresent()) {
            throw new IllegalStateException("ALREADY_LINKED: El atleta ya está vinculado a este maestro");
        }
        return repo.save(CoachAthleteRelationship.builder()
                .coachId(coachId)
                .athleteId(athleteId)
                .invitationId(invitationId)
                .linkedAt(Instant.now())
                .build());
    }

    public void unlinkByCoach(Long coachId, Long athleteId) {
        repo.deleteByCoachIdAndAthleteId(coachId, athleteId);
    }

    public void unlinkByAthlete(Long athleteId, Long coachId) {
        repo.deleteByCoachIdAndAthleteId(coachId, athleteId);
    }

    public List<CoachAthleteRelationship> getAthletes(Long coachId) {
        return repo.findAllByCoachId(coachId);
    }

    public List<CoachAthleteRelationship> getCoaches(Long athleteId) {
        return repo.findAllByAthleteId(athleteId);
    }

    public boolean isLinked(Long coachId, Long athleteId) {
        return repo.existsByCoachIdAndAthleteId(coachId, athleteId);
    }
}
