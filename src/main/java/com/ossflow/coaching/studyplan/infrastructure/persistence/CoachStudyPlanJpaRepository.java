package com.ossflow.coaching.studyplan.infrastructure.persistence;

import com.ossflow.coaching.studyplan.domain.StudyPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CoachStudyPlanJpaRepository extends JpaRepository<CoachStudyPlanEntity, Long> {

    List<CoachStudyPlanEntity> findByCoachIdAndAthleteIdOrderByCreatedAtDesc(Long coachId, Long athleteId);

    List<CoachStudyPlanEntity> findByAthleteIdAndStatusOrderByCreatedAtDesc(Long athleteId, StudyPlanStatus status);

    @Modifying
    @Query("UPDATE CoachStudyPlanEntity p SET p.status = :status WHERE p.id = :id AND p.coachId = :coachId")
    int updateStatus(Long id, Long coachId, StudyPlanStatus status);

    @Modifying
    @Query("UPDATE CoachStudyPlanEntity p SET p.title = :title, p.description = :description WHERE p.id = :id AND p.coachId = :coachId")
    int updateContent(Long id, Long coachId, String title, String description);

    @Modifying
    @Query("UPDATE CoachStudyPlanEntity p SET p.viewedByAthlete = true WHERE p.id = :id")
    void markViewed(Long id);
}
