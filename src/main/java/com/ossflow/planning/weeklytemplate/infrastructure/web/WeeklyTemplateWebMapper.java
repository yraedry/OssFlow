package com.ossflow.planning.weeklytemplate.infrastructure.web;

import com.ossflow.planning.weeklytemplate.domain.DayEntry;
import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.DayEntryDto;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.SaveWeeklyTemplateRequest;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.WeeklyTemplateResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeeklyTemplateWebMapper {

    public WeeklyTemplateResponse toResponse(WeeklyTemplate t) {
        List<DayEntryDto> days = t.days().stream()
                .map(d -> new DayEntryDto(d.dayOfWeek(), d.bjj(), d.strength(), d.cardio(), d.mobility(), d.flexibility()))
                .toList();
        return new WeeklyTemplateResponse(t.id(), days, t.updatedAt());
    }

    public WeeklyTemplate fromRequest(SaveWeeklyTemplateRequest req) {
        List<DayEntry> days = req.days().stream()
                .map(d -> DayEntry.builder()
                        .dayOfWeek(d.dayOfWeek())
                        .bjj(d.bjj())
                        .strength(d.strength())
                        .cardio(d.cardio())
                        .mobility(d.mobility())
                        .flexibility(d.flexibility())
                        .build())
                .toList();
        return WeeklyTemplate.builder().days(days).build();
    }
}
