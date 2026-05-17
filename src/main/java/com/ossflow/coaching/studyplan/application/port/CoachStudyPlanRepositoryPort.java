package com.ossflow.coaching.studyplan.application.port;

import com.ossflow.coaching.studyplan.domain.CoachStudyBlock;
import com.ossflow.coaching.studyplan.domain.CoachStudyItem;
import com.ossflow.coaching.studyplan.domain.CoachStudyPlan;
import com.ossflow.coaching.studyplan.domain.StudyPlanStatus;

import java.util.List;
import java.util.Optional;

public interface CoachStudyPlanRepositoryPort {

    CoachStudyPlan savePlan(CoachStudyPlan plan);

    Optional<CoachStudyPlan> findPlanById(Long id);

    List<CoachStudyPlan> findPlansByCoachAndAthlete(Long coachId, Long athleteId);

    List<CoachStudyPlan> findPublishedPlansForAthlete(Long athleteId);

    int updatePlanStatus(Long id, Long coachId, StudyPlanStatus status);

    int updatePlanContent(Long id, Long coachId, String title, String description);

    void deletePlan(Long id);

    CoachStudyBlock saveBlock(CoachStudyBlock block);

    void deleteBlock(Long blockId, Long planId, Long coachId);

    CoachStudyItem saveItem(CoachStudyItem item);

    void deleteItem(Long itemId, Long blockId, Long coachId);

    void reorderItems(Long blockId, List<Long> orderedItemIds);

    void reorderBlocks(Long planId, List<Long> orderedBlockIds);

    void markViewedByAthlete(Long planId);
}
