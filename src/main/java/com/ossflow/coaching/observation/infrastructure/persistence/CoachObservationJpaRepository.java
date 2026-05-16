package com.ossflow.coaching.observation.infrastructure.persistence;

import com.ossflow.coaching.observation.application.RadarRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoachObservationJpaRepository extends JpaRepository<CoachObservationEntity, Long> {

    List<CoachObservationEntity> findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(Long coachId, Long athleteId);

    Optional<CoachObservationEntity> findByIdAndCoachId(Long id, Long coachId);

    @Query("""
        SELECT new com.ossflow.coaching.observation.application.RadarRow(
            o.techniqueFamily,
            SUM(CASE WHEN o.tone = com.ossflow.coaching.observation.domain.Tone.POSITIVE THEN 1L
                     WHEN o.tone = com.ossflow.coaching.observation.domain.Tone.NEGATIVE THEN -1L
                     ELSE 0L END)
        )
        FROM CoachObservationEntity o
        WHERE o.coachId = :coachId
          AND o.athleteId = :athleteId
          AND o.techniqueFamily IS NOT NULL
        GROUP BY o.techniqueFamily
    """)
    List<RadarRow> aggregateRadar(@Param("coachId") Long coachId, @Param("athleteId") Long athleteId);
}
