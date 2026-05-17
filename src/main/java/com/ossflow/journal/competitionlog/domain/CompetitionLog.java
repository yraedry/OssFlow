package com.ossflow.journal.competitionlog.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record CompetitionLog(
        Long id,
        Long ownerId,
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
        List<CompetitionMatch> matches,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
