package com.ossflow.journal.competitionlog.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

public record AddCompetitionMatchRequest(
        Integer matchOrder,
        @Size(max = 200) String opponentName,
        @Size(max = 200) String opponentTeam,
        @Size(max = 20) String outcome,
        @Size(max = 50) String method,
        Long submissionTechniqueId,
        @Size(max = 10000) String notesMarkdown
) {}
