package com.ossflow.coaching.classplan.application.port;

import com.ossflow.coaching.classplan.domain.ClassPlan;
import com.ossflow.coaching.classplan.domain.ClassPlanStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClassPlanRepositoryPort {

    ClassPlan save(ClassPlan plan);

    Optional<ClassPlan> findById(Long id);

    List<ClassPlan> findByCoachIdAndGymId(Long coachId, Long gymId);

    int updateMeta(Long id, Long coachId, String title, String description,
                   LocalDate scheduledDate, Integer durationMinutes, String modality,
                   ClassPlanStatus status);

    void deleteById(Long id);
}
