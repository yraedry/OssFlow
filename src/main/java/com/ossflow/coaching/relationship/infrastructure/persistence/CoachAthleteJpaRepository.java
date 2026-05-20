package com.ossflow.coaching.relationship.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CoachAthleteJpaRepository extends JpaRepository<CoachAthleteEntity, Long> {
    Optional<CoachAthleteEntity> findByCoachIdAndAthleteId(Long coachId, Long athleteId);
    List<CoachAthleteEntity> findAllByCoachId(Long coachId);
    List<CoachAthleteEntity> findAllByAthleteId(Long athleteId);
    void deleteByCoachIdAndAthleteId(Long coachId, Long athleteId);
    boolean existsByCoachIdAndAthleteId(Long coachId, Long athleteId);
}
