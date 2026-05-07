package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeeklyTemplateJpaRepository extends JpaRepository<WeeklyTemplateEntity, Long> {
    Optional<WeeklyTemplateEntity> findByOwnerId(Long ownerId);
}
