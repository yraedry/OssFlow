package com.ossflow.coaching.observation.infrastructure.persistence;

import com.ossflow.coaching.observation.application.port.CoachObservationRepositoryPort;
import com.ossflow.coaching.observation.domain.CoachObservation;
import com.ossflow.coaching.observation.domain.RadarRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoachObservationPersistenceAdapter implements CoachObservationRepositoryPort {

    private final CoachObservationJpaRepository jpa;
    private final CoachObservationPersistenceMapper mapper;

    @Override
    public CoachObservation save(CoachObservation o) {
        return mapper.toDomain(jpa.save(mapper.toEntity(o)));
    }

    @Override
    public java.util.Optional<CoachObservation> findByIdAndCoachId(Long id, Long coachId) {
        return jpa.findByIdAndCoachId(id, coachId).map(mapper::toDomain);
    }

    @Override
    public List<CoachObservation> findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(Long coachId, Long athleteId) {
        return jpa.findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(coachId, athleteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public int deleteByIdAndCoachId(Long id, Long coachId) {
        return jpa.deleteByIdAndCoachId(id, coachId);
    }

    @Override
    public List<RadarRow> aggregateRadar(Long coachId, Long athleteId) {
        return jpa.aggregateRadar(coachId, athleteId);
    }
}
