package com.ossflow.journal.competitionlog.infrastructure.web.dto;

public record CompetitionMatchResponse(
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
