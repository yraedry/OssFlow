package com.ossflow.coaching.studyplan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CoachStudyItemJpaRepository extends JpaRepository<CoachStudyItemEntity, Long> {

    @Modifying
    @Query("DELETE FROM CoachStudyItemEntity i WHERE i.id = :itemId AND i.block.id = :blockId")
    void deleteByIdAndBlockId(Long itemId, Long blockId);

    @Modifying
    @Query("UPDATE CoachStudyItemEntity i SET i.itemOrder = :order WHERE i.id = :id")
    void updateOrder(Long id, int order);
}
