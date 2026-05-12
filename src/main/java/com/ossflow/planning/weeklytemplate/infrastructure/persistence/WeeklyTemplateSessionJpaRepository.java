package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WeeklyTemplateSessionJpaRepository extends JpaRepository<WeeklyTemplateSessionEntity, Long> {

    List<WeeklyTemplateSessionEntity> findByTemplateId(Long templateId);

    @Modifying
    @Query("DELETE FROM WeeklyTemplateSessionEntity s WHERE s.templateId = :templateId")
    void deleteByTemplateId(Long templateId);
}
