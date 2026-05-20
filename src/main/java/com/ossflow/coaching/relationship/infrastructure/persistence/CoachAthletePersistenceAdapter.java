package com.ossflow.coaching.relationship.infrastructure.persistence;

import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoachAthletePersistenceAdapter implements CoachAthleteRepositoryPort {
    private final CoachAthleteJpaRepository jpa;
    private final CoachAthletePersistenceMapper mapper;

    @Override public CoachAthleteRelationship save(CoachAthleteRelationship r) {
        return mapper.toDomain(jpa.save(mapper.toEntity(r)));
    }
    @Override public Optional<CoachAthleteRelationship> findByCoachIdAndAthleteId(Long c, Long a) {
        return jpa.findByCoachIdAndAthleteId(c, a).map(mapper::toDomain);
    }
    @Override public List<CoachAthleteRelationship> findAllByCoachId(Long coachId) {
        return jpa.findAllByCoachId(coachId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<CoachAthleteRelationship> findAllByAthleteId(Long athleteId) {
        return jpa.findAllByAthleteId(athleteId).stream().map(mapper::toDomain).toList();
    }
    @Override @Transactional
    public void deleteByCoachIdAndAthleteId(Long coachId, Long athleteId) {
        jpa.deleteByCoachIdAndAthleteId(coachId, athleteId);
    }
    @Override public boolean existsByCoachIdAndAthleteId(Long coachId, Long athleteId) {
        return jpa.existsByCoachIdAndAthleteId(coachId, athleteId);
    }
}
