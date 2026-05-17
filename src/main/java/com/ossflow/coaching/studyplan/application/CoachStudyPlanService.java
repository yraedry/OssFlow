package com.ossflow.coaching.studyplan.application;

import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.coaching.studyplan.application.port.CoachStudyPlanRepositoryPort;
import com.ossflow.coaching.studyplan.domain.*;
import com.ossflow.coaching.studyplan.infrastructure.web.dto.*;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachStudyPlanService {

    private final CoachStudyPlanRepositoryPort repo;
    private final CoachAthleteRepositoryPort coachAthleteRepo;
    private final TechniqueRepositoryPort techniqueRepo;

    @Transactional
    public CoachStudyPlan createPlan(Long coachId, Long athleteId, String title) {
        requireLinked(coachId, athleteId);
        return repo.savePlan(CoachStudyPlan.builder()
                .coachId(coachId)
                .athleteId(athleteId)
                .title(title)
                .status(StudyPlanStatus.DRAFT)
                .viewedByAthlete(false)
                .build());
    }

    public List<CoachStudyPlan> listForCoach(Long coachId, Long athleteId) {
        requireLinked(coachId, athleteId);
        return repo.findPlansByCoachAndAthlete(coachId, athleteId);
    }

    public List<CoachStudyPlan> listPublishedForAthlete(Long athleteId) {
        return repo.findPublishedPlansForAthlete(athleteId);
    }

    public CoachStudyPlan getPlan(Long planId, Long requesterId, boolean isCoach) {
        var plan = repo.findPlanById(planId)
                .orElseThrow(() -> new NotFoundException("PLAN_NOT_FOUND", "Plan not found"));
        if (isCoach && !plan.coachId().equals(requesterId)) {
            throw new ForbiddenException("PLAN_ACCESS_DENIED", "Not your plan");
        }
        if (!isCoach) {
            if (!plan.athleteId().equals(requesterId) || plan.status() != StudyPlanStatus.PUBLISHED) {
                throw new ForbiddenException("PLAN_ACCESS_DENIED", "Plan not available");
            }
            repo.markViewedByAthlete(planId);
        }
        return plan;
    }

    @Transactional
    public void updatePlanContent(Long planId, Long coachId, UpdatePlanContentRequest req) {
        var plan = requireCoachPlan(planId, coachId);
        repo.updatePlanContent(planId, coachId, req.title(), req.description());
    }

    @Transactional
    public void publishPlan(Long planId, Long coachId) {
        requireCoachPlan(planId, coachId);
        int rows = repo.updatePlanStatus(planId, coachId, StudyPlanStatus.PUBLISHED);
        if (rows == 0) throw new NotFoundException("PLAN_NOT_FOUND", "Plan not found");
    }

    @Transactional
    public void unpublishPlan(Long planId, Long coachId) {
        requireCoachPlan(planId, coachId);
        int rows = repo.updatePlanStatus(planId, coachId, StudyPlanStatus.DRAFT);
        if (rows == 0) throw new NotFoundException("PLAN_NOT_FOUND", "Plan not found");
    }

    @Transactional
    public void deletePlan(Long planId, Long coachId) {
        var plan = requireCoachPlan(planId, coachId);
        if (plan.status() == StudyPlanStatus.PUBLISHED && plan.viewedByAthlete()) {
            throw new ForbiddenException("PLAN_ALREADY_VIEWED", "Cannot delete a plan already seen by athlete");
        }
        repo.deletePlan(planId);
    }

    @Transactional
    public CoachStudyBlock addBlock(Long planId, Long coachId, String title) {
        requireCoachPlan(planId, coachId);
        return repo.saveBlock(CoachStudyBlock.builder()
                .planId(planId)
                .title(title != null ? title : "")
                .blockOrder(0)
                .build());
    }

    @Transactional
    public void deleteBlock(Long planId, Long blockId, Long coachId) {
        requireCoachPlan(planId, coachId);
        repo.deleteBlock(blockId, planId, coachId);
    }

    @Transactional
    public void reorderBlocks(Long planId, Long coachId, List<Long> orderedBlockIds) {
        requireCoachPlan(planId, coachId);
        repo.reorderBlocks(planId, orderedBlockIds);
    }

    @Transactional
    public CoachStudyItem addTextItem(Long planId, Long blockId, Long coachId, String content) {
        requireCoachPlan(planId, coachId);
        return repo.saveItem(CoachStudyItem.builder()
                .blockId(blockId)
                .itemType(StudyItemType.TEXT)
                .content(content)
                .itemOrder(0)
                .build());
    }

    @Transactional
    public CoachStudyItem addTechniqueItem(Long planId, Long blockId, Long coachId, Long techniqueId) {
        requireCoachPlan(planId, coachId);
        var technique = techniqueRepo.findById(techniqueId, coachId)
                .orElseThrow(() -> new NotFoundException("TECHNIQUE_NOT_FOUND", "Technique not found"));
        return repo.saveItem(CoachStudyItem.builder()
                .blockId(blockId)
                .itemType(StudyItemType.TECHNIQUE)
                .techniqueId(techniqueId)
                .techniqueName(technique.name())
                .itemOrder(0)
                .build());
    }

    @Transactional
    public void deleteItem(Long planId, Long blockId, Long itemId, Long coachId) {
        requireCoachPlan(planId, coachId);
        repo.deleteItem(itemId, blockId, coachId);
    }

    @Transactional
    public void reorderItems(Long planId, Long blockId, Long coachId, List<Long> orderedItemIds) {
        requireCoachPlan(planId, coachId);
        repo.reorderItems(blockId, orderedItemIds);
    }

    private CoachStudyPlan requireCoachPlan(Long planId, Long coachId) {
        return repo.findPlanById(planId)
                .filter(p -> p.coachId().equals(coachId))
                .orElseThrow(() -> new ForbiddenException("PLAN_ACCESS_DENIED", "Not your plan"));
    }

    private void requireLinked(Long coachId, Long athleteId) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, athleteId)) {
            throw new ForbiddenException("NOT_YOUR_ATHLETE", "Not your athlete");
        }
    }
}
