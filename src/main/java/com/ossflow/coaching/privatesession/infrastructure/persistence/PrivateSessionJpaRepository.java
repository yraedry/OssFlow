package com.ossflow.coaching.privatesession.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PrivateSessionJpaRepository extends JpaRepository<PrivateSessionEntity, Long> {

    Optional<PrivateSessionEntity> findByIdAndCoachId(Long id, Long coachId);

    List<PrivateSessionEntity> findByCoachIdAndAthleteIdOrderBySessionDateDesc(Long coachId, Long athleteId);

    List<PrivateSessionEntity> findByCoachIdOrderBySessionDateDesc(Long coachId);

    List<PrivateSessionEntity> findByAthleteIdOrderBySessionDateDesc(Long athleteId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PrivateSessionEntity e WHERE e.id = :id AND e.coachId = :coachId")
    int deleteByIdAndCoachId(@Param("id") Long id, @Param("coachId") Long coachId);
}
