package com.ossflow.journal.competitionlog.infrastructure.web.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CompetitionLogResponse(
        Long id,
        String eventName,
        LocalDate eventDate,
        String weightCategory,
        String categoryAge,
        String location,
        String giNogi,
        Integer totalMatches,
        Integer winsCount,
        Integer lossesCount,
        String result,
        String analysisMarkdown,
        List<CompetitionMatchResponse> matches,
        Instant createdAt,
        Instant updatedAt
) {}
