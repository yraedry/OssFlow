package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import com.ossflow.planning.weeklytemplate.domain.DayEntry;
import com.ossflow.planning.weeklytemplate.domain.SessionSlot;
import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WeeklyTemplatePersistenceMapper {

    public WeeklyTemplate toDomain(WeeklyTemplateEntity e, List<WeeklyTemplateSessionEntity> sessions) {
        Map<DayOfWeek, List<SessionSlot>> byDay = sessions.stream()
                .collect(Collectors.groupingBy(
                        WeeklyTemplateSessionEntity::getDayOfWeek,
                        Collectors.mapping(
                                s -> SessionSlot.builder().type(s.getSessionType()).time(s.getTime()).build(),
                                Collectors.toList()
                        )
                ));

        List<DayEntry> days = byDay.entrySet().stream()
                .map(entry -> DayEntry.builder()
                        .dayOfWeek(entry.getKey())
                        .sessions(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(DayEntry::dayOfWeek))
                .toList();

        return WeeklyTemplate.builder()
                .id(e.getId())
                .ownerId(e.getOwnerId())
                .days(days)
                .createdAt(e.getCreatedAtInstant())
                .updatedAt(e.getUpdatedAtInstant())
                .build();
    }

    public WeeklyTemplateEntity toEntity(WeeklyTemplate d) {
        WeeklyTemplateEntity entity = new WeeklyTemplateEntity();
        entity.setOwnerId(d.ownerId());
        return entity;
    }

    public List<WeeklyTemplateSessionEntity> toSessionEntities(Long templateId, List<DayEntry> days) {
        return days.stream()
                .flatMap(day -> day.sessions().stream().map(slot ->
                        WeeklyTemplateSessionEntity.builder()
                                .templateId(templateId)
                                .dayOfWeek(day.dayOfWeek())
                                .sessionType(slot.type())
                                .time(slot.time())
                                .build()
                ))
                .toList();
    }
}
