package com.ossflow.coaching.classplan.infrastructure.persistence;

import com.ossflow.coaching.classplan.application.port.ClassPlanRepositoryPort;
import com.ossflow.coaching.classplan.domain.ClassPlan;
import com.ossflow.coaching.classplan.domain.ClassPlanStatus;
import com.ossflow.coaching.studyplan.domain.CoachStudyBlock;
import com.ossflow.coaching.studyplan.domain.CoachStudyItem;
import com.ossflow.coaching.studyplan.infrastructure.persistence.CoachStudyBlockEntity;
import com.ossflow.coaching.studyplan.infrastructure.persistence.CoachStudyItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClassPlanPersistenceAdapter implements ClassPlanRepositoryPort {

    private final ClassPlanJpaRepository jpa;

    @Override
    @Transactional
    public ClassPlan save(ClassPlan plan) {
        var entity = toEntity(plan);
        return toDomain(jpa.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassPlan> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassPlan> findByCoachIdAndGymId(Long coachId, Long gymId) {
        return jpa.findByCoachIdAndGymId(coachId, gymId).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public int updateMeta(Long id, Long coachId, String title, String description,
                          LocalDate scheduledDate, Integer durationMinutes, String modality,
                          ClassPlanStatus status) {
        return jpa.updateMeta(id, coachId, title, description, scheduledDate, durationMinutes, modality, status);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    // ---- mapping ----

    private ClassPlanEntity toEntity(ClassPlan plan) {
        return ClassPlanEntity.builder()
                .id(plan.id())
                .coachId(plan.coachId())
                .gymId(plan.gymId())
                .title(plan.title())
                .description(plan.description())
                .scheduledDate(plan.scheduledDate())
                .durationMinutes(plan.durationMinutes())
                .modality(plan.modality())
                .status(plan.status() != null ? plan.status() : ClassPlanStatus.DRAFT)
                .build();
    }

    private ClassPlan toDomain(ClassPlanEntity e) {
        var blocks = e.getBlocks() != null
                ? e.getBlocks().stream().map(this::toBlockDomain).toList()
                : List.<CoachStudyBlock>of();
        return ClassPlan.builder()
                .id(e.getId())
                .coachId(e.getCoachId())
                .gymId(e.getGymId())
                .title(e.getTitle())
                .description(e.getDescription())
                .scheduledDate(e.getScheduledDate())
                .durationMinutes(e.getDurationMinutes())
                .modality(e.getModality())
                .status(e.getStatus())
                .blocks(blocks)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private CoachStudyBlock toBlockDomain(CoachStudyBlockEntity e) {
        var items = e.getItems() != null
                ? e.getItems().stream().map(this::toItemDomain).toList()
                : List.<CoachStudyItem>of();
        return CoachStudyBlock.builder()
                .id(e.getId())
                .planId(e.getPlan() != null ? e.getPlan().getId() : null)
                .title(e.getTitle())
                .blockOrder(e.getBlockOrder())
                .items(items)
                .build();
    }

    private CoachStudyItem toItemDomain(CoachStudyItemEntity e) {
        return CoachStudyItem.builder()
                .id(e.getId())
                .blockId(e.getBlock() != null ? e.getBlock().getId() : null)
                .itemOrder(e.getItemOrder())
                .itemType(e.getItemType())
                .content(e.getContent())
                .techniqueId(e.getTechniqueId())
                .techniqueName(e.getTechniqueName())
                .build();
    }
}
