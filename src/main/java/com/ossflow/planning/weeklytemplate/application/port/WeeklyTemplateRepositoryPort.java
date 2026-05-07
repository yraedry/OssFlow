package com.ossflow.planning.weeklytemplate.application.port;

import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import java.util.Optional;

public interface WeeklyTemplateRepositoryPort {
    Optional<WeeklyTemplate> findByOwnerId(Long ownerId);
    WeeklyTemplate save(WeeklyTemplate template);
}
