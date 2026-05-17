package com.ossflow.coaching.classplan.infrastructure.persistence;

import com.ossflow.coaching.classplan.domain.ClassPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ClassPlanJpaRepository extends JpaRepository<ClassPlanEntity, Long> {

    @Query("SELECT cp FROM ClassPlanEntity cp WHERE cp.coachId = :coachId AND cp.gymId = :gymId " +
           "ORDER BY cp.scheduledDate DESC NULLS LAST, cp.createdAt DESC")
    List<ClassPlanEntity> findByCoachIdAndGymId(@Param("coachId") Long coachId, @Param("gymId") Long gymId);

    @Modifying
    @Query("UPDATE ClassPlanEntity cp SET cp.title = :title, cp.description = :description, " +
           "cp.scheduledDate = :scheduledDate, cp.durationMinutes = :durationMinutes, " +
           "cp.modality = :modality, cp.status = :status " +
           "WHERE cp.id = :id AND cp.coachId = :coachId")
    int updateMeta(@Param("id") Long id,
                   @Param("coachId") Long coachId,
                   @Param("title") String title,
                   @Param("description") String description,
                   @Param("scheduledDate") LocalDate scheduledDate,
                   @Param("durationMinutes") Integer durationMinutes,
                   @Param("modality") String modality,
                   @Param("status") ClassPlanStatus status);
}
