package com.ossflow.planning.weeklytemplate.application.port;

import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import java.util.Optional;

public interface WeeklyTemplateRepositoryPort {
    /** Each owner has at most one template. Returns empty if not yet configured. */
    Optional<WeeklyTemplate> findByOwnerId(Long ownerId);
    WeeklyTemplate save(WeeklyTemplate template);
}
