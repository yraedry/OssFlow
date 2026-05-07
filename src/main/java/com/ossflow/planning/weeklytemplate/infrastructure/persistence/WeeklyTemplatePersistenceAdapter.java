package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import com.ossflow.planning.weeklytemplate.application.port.WeeklyTemplateRepositoryPort;
import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WeeklyTemplatePersistenceAdapter implements WeeklyTemplateRepositoryPort {

    private final WeeklyTemplateJpaRepository jpa;
    private final WeeklyTemplatePersistenceMapper mapper;

    @Override
    public Optional<WeeklyTemplate> findByOwnerId(Long ownerId) {
        return jpa.findByOwnerId(ownerId).map(mapper::toDomain);
    }

    @Transactional
    @Override
    public WeeklyTemplate save(WeeklyTemplate template) {
        WeeklyTemplateEntity entity = jpa.findByOwnerId(template.ownerId())
                .map(existing -> {
                    existing.setDays(template.days());
                    return existing;
                })
                .orElse(mapper.toEntity(template));
        return mapper.toDomain(jpa.save(entity));
    }
}
