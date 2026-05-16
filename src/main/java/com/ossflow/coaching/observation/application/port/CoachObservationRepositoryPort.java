package com.ossflow.coaching.observation.application.port;

import com.ossflow.coaching.observation.application.RadarRow;
import com.ossflow.coaching.observation.domain.CoachObservation;

import java.util.List;
import java.util.Optional;

public interface CoachObservationRepositoryPort {
    CoachObservation save(CoachObservation observation);
    List<CoachObservation> findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(Long coachId, Long athleteId);
    Optional<CoachObservation> findByIdAndCoachId(Long id, Long coachId);
    void deleteById(Long id);
    List<RadarRow> aggregateRadar(Long coachId, Long athleteId);
}
