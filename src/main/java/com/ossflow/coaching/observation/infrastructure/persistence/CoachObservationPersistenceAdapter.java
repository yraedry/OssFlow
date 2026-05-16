package com.ossflow.coaching.observation.infrastructure.persistence;

import com.ossflow.coaching.observation.application.RadarRow;
import com.ossflow.coaching.observation.application.port.CoachObservationRepositoryPort;
import com.ossflow.coaching.observation.domain.CoachObservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoachObservationPersistenceAdapter implements CoachObservationRepositoryPort {

    private final CoachObservationJpaRepository jpa;
    private final CoachObservationMapper mapper;

    @Override
    public CoachObservation save(CoachObservation o) {
        return mapper.toDomain(jpa.save(mapper.toEntity(o)));
    }

    @Override
    public List<CoachObservation> findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(Long coachId, Long athleteId) {
        return jpa.findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(coachId, athleteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<CoachObservation> findByIdAndCoachId(Long id, Long coachId) {
        return jpa.findByIdAndCoachId(id, coachId).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public List<RadarRow> aggregateRadar(Long coachId, Long athleteId) {
        return jpa.aggregateRadar(coachId, athleteId);
    }
}
