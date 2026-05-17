package com.ossflow.coaching.classplan.application;

import com.ossflow.coaching.classplan.application.port.ClassPlanRepositoryPort;
import com.ossflow.coaching.classplan.domain.ClassPlan;
import com.ossflow.coaching.classplan.domain.ClassPlanStatus;
import com.ossflow.coaching.gym.application.port.GymRepositoryPort;
import com.ossflow.coaching.gym.domain.GymLocation;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassPlanService {

    private final ClassPlanRepositoryPort repo;
    private final GymRepositoryPort gymRepo;

    @Transactional
    public ClassPlan create(Long coachId, Long gymId, String title, String description,
                            LocalDate scheduledDate, Integer durationMinutes, String modality) {
        requireGymOwned(gymId, coachId);
        var plan = ClassPlan.builder()
                .coachId(coachId)
                .gymId(gymId)
                .title(title)
                .description(description)
                .scheduledDate(scheduledDate)
                .durationMinutes(durationMinutes)
                .modality(modality)
                .status(ClassPlanStatus.DRAFT)
                .blocks(List.of())
                .build();
        return repo.save(plan);
    }

    @Transactional(readOnly = true)
    public List<ClassPlan> list(Long coachId, Long gymId) {
        requireGymOwned(gymId, coachId);
        return repo.findByCoachIdAndGymId(coachId, gymId);
    }

    @Transactional(readOnly = true)
    public ClassPlan get(Long planId, Long coachId) {
        return requirePlanOwned(planId, coachId);
    }

    @Transactional
    public ClassPlan update(Long planId, Long coachId, String title, String description,
                            LocalDate scheduledDate, Integer durationMinutes, String modality,
                            ClassPlanStatus status) {
        var existing = requirePlanOwned(planId, coachId);
        int rows = repo.updateMeta(planId, coachId, title, description, scheduledDate, durationMinutes, modality, status);
        if (rows == 0) throw new NotFoundException("CLASS_PLAN_NOT_FOUND", "Class plan not found");
        return existing.toBuilder()
                .title(title)
                .description(description)
                .scheduledDate(scheduledDate)
                .durationMinutes(durationMinutes)
                .modality(modality)
                .status(status)
                .build();
    }

    @Transactional
    public void delete(Long planId, Long coachId) {
        requirePlanOwned(planId, coachId);
        repo.deleteById(planId);
    }

    // ---- private helpers ----

    private GymLocation requireGymOwned(Long gymId, Long coachId) {
        return gymRepo.findById(gymId)
                .filter(g -> g.coachId().equals(coachId))
                .orElseThrow(() -> new ForbiddenException("GYM_ACCESS_DENIED", "Not your gym"));
    }

    private ClassPlan requirePlanOwned(Long planId, Long coachId) {
        var plan = repo.findById(planId)
                .orElseThrow(() -> new NotFoundException("CLASS_PLAN_NOT_FOUND", "Class plan not found"));
        if (!plan.coachId().equals(coachId)) {
            throw new ForbiddenException("CLASS_PLAN_ACCESS_DENIED", "Not your class plan");
        }
        return plan;
    }
}
