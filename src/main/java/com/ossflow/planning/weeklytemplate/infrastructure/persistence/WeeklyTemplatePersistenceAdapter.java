package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import com.ossflow.planning.weeklytemplate.application.port.WeeklyTemplateRepositoryPort;
import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WeeklyTemplatePersistenceAdapter implements WeeklyTemplateRepositoryPort {

    private final WeeklyTemplateJpaRepository jpa;
    private final WeeklyTemplateSessionJpaRepository sessionJpa;
    private final WeeklyTemplatePersistenceMapper mapper;

    @Override
    public Optional<WeeklyTemplate> findByOwnerId(Long ownerId) {
        return jpa.findByOwnerId(ownerId).map(entity -> {
            List<WeeklyTemplateSessionEntity> sessions = sessionJpa.findByTemplateId(entity.getId());
            return mapper.toDomain(entity, sessions);
        });
    }

    @Transactional
    @Override
    public void deleteByOwnerId(Long ownerId) {
        jpa.findByOwnerId(ownerId).ifPresent(entity -> {
            sessionJpa.deleteByTemplateId(entity.getId());
            jpa.delete(entity);
        });
    }

    @Transactional
    @Override
    public WeeklyTemplate save(WeeklyTemplate template) {
        WeeklyTemplateEntity entity = jpa.findByOwnerId(template.ownerId())
                .orElse(mapper.toEntity(template));
        WeeklyTemplateEntity saved = jpa.save(entity);

        sessionJpa.deleteByTemplateId(saved.getId());
        List<WeeklyTemplateSessionEntity> sessions = mapper.toSessionEntities(saved.getId(), template.days());
        sessionJpa.saveAll(sessions);

        return mapper.toDomain(saved, sessions);
    }
}
