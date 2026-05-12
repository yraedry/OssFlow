package com.ossflow.planning.weeklytemplate.infrastructure.web;

import com.ossflow.planning.weeklytemplate.domain.DayEntry;
import com.ossflow.planning.weeklytemplate.domain.SessionSlot;
import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.DayEntryDto;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.SaveWeeklyTemplateRequest;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.SessionSlotDto;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.WeeklyTemplateResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeeklyTemplateWebMapper {

    public WeeklyTemplateResponse toResponse(WeeklyTemplate t) {
        List<DayEntryDto> days = t.days().stream()
                .map(d -> new DayEntryDto(
                        d.dayOfWeek(),
                        d.sessions().stream()
                                .map(s -> new SessionSlotDto(s.type(), s.time()))
                                .toList()
                ))
                .toList();
        return new WeeklyTemplateResponse(t.id(), days, t.updatedAt());
    }

    public WeeklyTemplate fromRequest(SaveWeeklyTemplateRequest req) {
        List<DayEntry> days = req.days().stream()
                .map(d -> DayEntry.builder()
                        .dayOfWeek(d.dayOfWeek())
                        .sessions(d.sessions().stream()
                                .map(s -> SessionSlot.builder().type(s.type()).time(s.time()).build())
                                .toList())
                        .build())
                .toList();
        return WeeklyTemplate.builder().days(days).build();
    }
}
