package com.ossflow.coaching.relationship.application.port;

import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import java.util.List;
import java.util.Optional;

public interface CoachAthleteRepositoryPort {
    CoachAthleteRelationship save(CoachAthleteRelationship relationship);
    Optional<CoachAthleteRelationship> findByCoachIdAndAthleteId(Long coachId, Long athleteId);
    List<CoachAthleteRelationship> findAllByCoachId(Long coachId);
    List<CoachAthleteRelationship> findAllByAthleteId(Long athleteId);
    void deleteByCoachIdAndAthleteId(Long coachId, Long athleteId);
    boolean existsByCoachIdAndAthleteId(Long coachId, Long athleteId);
}
