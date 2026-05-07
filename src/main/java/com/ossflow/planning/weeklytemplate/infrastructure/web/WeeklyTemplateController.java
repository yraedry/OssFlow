package com.ossflow.planning.weeklytemplate.infrastructure.web;

import com.ossflow.planning.weeklytemplate.application.WeeklyTemplateService;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.SaveWeeklyTemplateRequest;
import com.ossflow.planning.weeklytemplate.infrastructure.web.dto.WeeklyTemplateResponse;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/planning/weekly-template")
@Validated
@RequiredArgsConstructor
public class WeeklyTemplateController {

    private final WeeklyTemplateService service;
    private final WeeklyTemplateWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public WeeklyTemplateResponse get() {
        return mapper.toResponse(service.getOrEmpty(currentOwner.id()));
    }

    @PutMapping
    public WeeklyTemplateResponse save(@Valid @RequestBody SaveWeeklyTemplateRequest req) {
        return mapper.toResponse(
                service.upsert(currentOwner.id(), mapper.fromRequest(req))
        );
    }
}
