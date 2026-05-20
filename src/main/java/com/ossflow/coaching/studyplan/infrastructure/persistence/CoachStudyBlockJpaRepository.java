package com.ossflow.coaching.studyplan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CoachStudyBlockJpaRepository extends JpaRepository<CoachStudyBlockEntity, Long> {

    @Modifying
    @Query("DELETE FROM CoachStudyBlockEntity b WHERE b.id = :blockId AND b.plan.id = :planId")
    void deleteByIdAndPlanId(Long blockId, Long planId);

    @Modifying
    @Query("UPDATE CoachStudyBlockEntity b SET b.blockOrder = :order WHERE b.id = :id")
    void updateOrder(Long id, int order);

    @Modifying
    @Query("UPDATE CoachStudyBlockEntity b SET b.title = :title WHERE b.id = :blockId AND b.plan.id = :planId")
    int updateTitle(Long blockId, Long planId, String title);

    @Modifying
    @Query("DELETE FROM CoachStudyBlockEntity b WHERE b.id = :blockId AND b.classPlan.id = :classPlanId")
    void deleteByIdAndClassPlanId(Long blockId, Long classPlanId);

    @Modifying
    @Query("UPDATE CoachStudyBlockEntity b SET b.title = :title WHERE b.id = :blockId AND b.classPlan.id = :classPlanId")
    int updateTitleByIdAndClassPlanId(Long blockId, Long classPlanId, String title);
}
