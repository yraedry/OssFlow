package com.ossflow.journal.competitionlog.infrastructure.web.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CompetitionLogResponse(
        Long id,
        String eventName,
        LocalDate eventDate,
        String weightCategory,
        Integer totalMatches,
        String result,
        String analysisMarkdown,
        List<CompetitionMatchResponse> matches,
        Instant createdAt,
        Instant updatedAt
) {}
