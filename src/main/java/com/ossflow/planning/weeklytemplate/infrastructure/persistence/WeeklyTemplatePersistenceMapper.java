package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeeklyTemplatePersistenceMapper {

    public WeeklyTemplate toDomain(WeeklyTemplateEntity e) {
        return WeeklyTemplate.builder()
                .id(e.getId())
                .ownerId(e.getOwnerId())
                .days(e.getDays() != null ? List.copyOf(e.getDays()) : List.of())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public WeeklyTemplateEntity toEntity(WeeklyTemplate d) {
        WeeklyTemplateEntity entity = new WeeklyTemplateEntity();
        entity.setOwnerId(d.ownerId());
        entity.setDays(d.days());
        return entity;
    }
}
