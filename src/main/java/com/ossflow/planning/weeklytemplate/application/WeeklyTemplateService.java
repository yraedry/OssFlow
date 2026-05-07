package com.ossflow.planning.weeklytemplate.application;

import com.ossflow.planning.weeklytemplate.application.port.WeeklyTemplateRepositoryPort;
import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyTemplateService {

    private final WeeklyTemplateRepositoryPort repository;

    public WeeklyTemplate getOrEmpty(Long ownerId) {
        return repository.findByOwnerId(ownerId)
                .orElse(WeeklyTemplate.builder().ownerId(ownerId).days(List.of()).build());
    }

    public WeeklyTemplate upsert(Long ownerId, WeeklyTemplate incoming) {
        WeeklyTemplate toSave = incoming.toBuilder().ownerId(ownerId).build();
        WeeklyTemplate saved = repository.save(toSave);
        log.info("WeeklyTemplate upserted ownerId={} id={}", saved.ownerId(), saved.id());
        return saved;
    }
}
