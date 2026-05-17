package com.ossflow.coaching.observation.application.port;

import com.ossflow.coaching.observation.domain.CoachObservation;
import com.ossflow.coaching.observation.domain.RadarRow;

import java.util.List;

public interface CoachObservationRepositoryPort {
    CoachObservation save(CoachObservation observation);
    java.util.Optional<CoachObservation> findByIdAndCoachId(Long id, Long coachId);
    List<CoachObservation> findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(Long coachId, Long athleteId);
    int deleteByIdAndCoachId(Long id, Long coachId);
    List<RadarRow> aggregateRadar(Long coachId, Long athleteId);
}
