package com.ossflow.coaching.studyplan.infrastructure.persistence;

import com.ossflow.coaching.classplan.infrastructure.persistence.ClassPlanJpaRepository;
import com.ossflow.coaching.studyplan.application.port.CoachStudyPlanRepositoryPort;
import com.ossflow.coaching.studyplan.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CoachStudyPlanPersistenceAdapter implements CoachStudyPlanRepositoryPort {

    private final CoachStudyPlanJpaRepository planRepo;
    private final CoachStudyBlockJpaRepository blockRepo;
    private final CoachStudyItemJpaRepository itemRepo;
    private final ClassPlanJpaRepository classPlanRepo;

    @Override
    @Transactional
    public CoachStudyPlan savePlan(CoachStudyPlan plan) {
        var entity = planRepo.save(toEntity(plan));
        return toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoachStudyPlan> findPlanById(Long id) {
        return planRepo.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachStudyPlan> findPlansByCoachAndAthlete(Long coachId, Long athleteId) {
        return planRepo.findByCoachIdAndAthleteIdOrderByCreatedAtDesc(coachId, athleteId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachStudyPlan> findPublishedPlansForAthlete(Long athleteId) {
        return planRepo.findByAthleteIdAndStatusOrderByCreatedAtDesc(athleteId, StudyPlanStatus.PUBLISHED)
                .stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public int updatePlanStatus(Long id, Long coachId, StudyPlanStatus status) {
        return planRepo.updateStatus(id, coachId, status);
    }

    @Override
    @Transactional
    public int updatePlanContent(Long id, Long coachId, String title, String description) {
        return planRepo.updateContent(id, coachId, title, description);
    }

    @Override
    public void deletePlan(Long id) {
        planRepo.deleteById(id);
    }

    @Override
    public CoachStudyBlock saveBlock(CoachStudyBlock block) {
        var entityBuilder = CoachStudyBlockEntity.builder()
                .title(block.title() != null ? block.title() : "")
                .blockOrder(block.blockOrder());

        if (block.planId() != null) {
            entityBuilder.plan(planRepo.getReferenceById(block.planId()));
        }
        if (block.classPlanId() != null) {
            entityBuilder.classPlan(classPlanRepo.getReferenceById(block.classPlanId()));
        }
        return toBlockDomain(blockRepo.save(entityBuilder.build()));
    }

    @Override
    @Transactional
    public void deleteBlock(Long blockId, Long planId, Long coachId) {
        blockRepo.deleteByIdAndPlanId(blockId, planId);
    }

    @Override
    public CoachStudyItem saveItem(CoachStudyItem item) {
        var block = blockRepo.getReferenceById(item.blockId());
        var entity = CoachStudyItemEntity.builder()
                .block(block)
                .itemOrder(item.itemOrder())
                .itemType(item.itemType())
                .content(item.content())
                .techniqueId(item.techniqueId())
                .techniqueName(item.techniqueName())
                .build();
        return toItemDomain(itemRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long blockId, Long coachId) {
        itemRepo.deleteByIdAndBlockId(itemId, blockId);
    }

    @Override
    @Transactional
    public void reorderItems(Long blockId, List<Long> orderedItemIds) {
        IntStream.range(0, orderedItemIds.size())
                .forEach(i -> itemRepo.updateOrder(orderedItemIds.get(i), i));
    }

    @Override
    @Transactional
    public void reorderBlocks(Long planId, List<Long> orderedBlockIds) {
        IntStream.range(0, orderedBlockIds.size())
                .forEach(i -> blockRepo.updateOrder(orderedBlockIds.get(i), i));
    }

    @Override
    @Transactional
    public void markViewedByAthlete(Long planId) {
        planRepo.markViewed(planId);
    }

    @Override
    @Transactional
    public int updateBlockTitle(Long blockId, Long planId, String title) {
        return blockRepo.updateTitle(blockId, planId, title);
    }

    private CoachStudyPlanEntity toEntity(CoachStudyPlan plan) {
        return CoachStudyPlanEntity.builder()
                .id(plan.id())
                .coachId(plan.coachId())
                .athleteId(plan.athleteId())
                .title(plan.title())
                .description(plan.description())
                .status(plan.status())
                .viewedByAthlete(plan.viewedByAthlete())
                .build();
    }

    private CoachStudyPlan toDomain(CoachStudyPlanEntity e) {
        var blocks = e.getBlocks() != null
                ? e.getBlocks().stream().map(this::toBlockDomain).toList()
                : List.<CoachStudyBlock>of();
        return CoachStudyPlan.builder()
                .id(e.getId())
                .coachId(e.getCoachId())
                .athleteId(e.getAthleteId())
                .title(e.getTitle())
                .description(e.getDescription())
                .status(e.getStatus())
                .viewedByAthlete(e.isViewedByAthlete())
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
                .classPlanId(e.getClassPlan() != null ? e.getClassPlan().getId() : null)
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
