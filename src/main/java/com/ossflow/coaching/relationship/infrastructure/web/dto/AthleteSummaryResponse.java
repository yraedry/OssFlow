package com.ossflow.coaching.relationship.infrastructure.web.dto;

import java.time.LocalDate;
import java.util.List;

public record AthleteSummaryResponse(
        Long athleteId,
        String displayName,
        String currentBelt,
        long daysInBelt,
        String academy,
        String ageCategory,
        Integer stripes,
        Double weight,
        String preferredModality,
        List<ActiveInjury> activeInjuries,
        List<RecentCompetition> recentCompetitions,
        LocalDate lastSessionDate,
        long daysSinceLastSession
) {
    public record ActiveInjury(String bodyPart, String severity, String status) {}
    public record RecentCompetition(String eventName, LocalDate eventDate, String result) {}
}
