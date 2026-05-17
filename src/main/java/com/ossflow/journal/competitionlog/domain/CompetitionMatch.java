package com.ossflow.journal.competitionlog.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record CompetitionMatch(
        Long id,
        Long competitionLogId,
        Integer matchOrder,
        String opponentName,
        String opponentTeam,
        String outcome,
        String method,
        Long submissionTechniqueId,
        String round,
        String techniqueText,
        String notesMarkdown
) {}
