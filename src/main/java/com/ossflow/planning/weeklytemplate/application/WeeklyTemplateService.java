package com.ossflow.planning.weeklytemplate.application;

import com.ossflow.planning.weeklytemplate.application.port.WeeklyTemplateRepositoryPort;
import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyTemplateService {

    private final WeeklyTemplateRepositoryPort repo;

    public WeeklyTemplate getOrEmpty(Long ownerId) {
        return repo.findByOwnerId(ownerId)
                .orElse(WeeklyTemplate.builder().ownerId(ownerId).days(List.of()).build());
    }

    public WeeklyTemplate upsert(Long ownerId, WeeklyTemplate incoming) {
        WeeklyTemplate toSave = incoming.toBuilder().ownerId(ownerId).build();
        return repo.save(toSave);
    }
}
