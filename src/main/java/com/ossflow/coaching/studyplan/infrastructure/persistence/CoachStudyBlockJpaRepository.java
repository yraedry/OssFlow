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
}
