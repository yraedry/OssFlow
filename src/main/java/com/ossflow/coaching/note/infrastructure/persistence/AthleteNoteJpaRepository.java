package com.ossflow.coaching.note.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AthleteNoteJpaRepository extends JpaRepository<AthleteNoteEntity, Long> {

    List<AthleteNoteEntity> findByCoachIdAndAthleteIdOrderByCreatedAtDesc(Long coachId, Long athleteId);

    Optional<AthleteNoteEntity> findByIdAndAthleteId(Long id, Long athleteId);

    @Query("SELECT e FROM AthleteNoteEntity e WHERE e.athleteId = :athleteId AND e.deletedAt IS NULL ORDER BY e.createdAt DESC")
    List<AthleteNoteEntity> findReceivedByAthleteId(@Param("athleteId") Long athleteId);

    @Query("SELECT COUNT(e) FROM AthleteNoteEntity e WHERE e.athleteId = :athleteId AND e.readAt IS NULL AND e.deletedAt IS NULL")
    long countUnreadByAthleteId(@Param("athleteId") Long athleteId);

    @Modifying
    @Transactional
    @Query("UPDATE AthleteNoteEntity e SET e.deletedAt = :now WHERE e.id = :id AND e.coachId = :coachId AND e.deletedAt IS NULL")
    int softDeleteByIdAndCoachId(@Param("id") Long id, @Param("coachId") Long coachId, @Param("now") Instant now);

    @Modifying
    @Transactional
    @Query("UPDATE AthleteNoteEntity e SET e.readAt = :now WHERE e.id = :id AND e.athleteId = :athleteId AND e.readAt IS NULL")
    int markRead(@Param("id") Long id, @Param("athleteId") Long athleteId, @Param("now") Instant now);

    @Modifying
    @Transactional
    @Query("DELETE FROM AthleteNoteEntity e WHERE e.deletedAt IS NOT NULL AND e.deletedAt < :threshold")
    int purgeOlderThan(@Param("threshold") Instant threshold);
}
