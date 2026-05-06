package com.ossflow.planning.portability;

import com.ossflow.planning.studyplan.application.StudyPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlanningExporter {

    private final StudyPlanService studyPlanService;

    public Map<String, Object> exportFor(Long ownerId) {
        var studyPlans = studyPlanService.list(ownerId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        return Map.of(
                "studyPlans", studyPlans
        );
    }
}
