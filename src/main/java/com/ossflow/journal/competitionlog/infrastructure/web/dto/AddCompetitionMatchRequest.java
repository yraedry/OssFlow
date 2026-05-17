package com.ossflow.journal.competitionlog.infrastructure.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddCompetitionMatchRequest(
        Integer matchOrder,
        @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String opponentName,
        @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String opponentTeam,
        @Size(max = 20) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String outcome,
        @Size(max = 50) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String method,
        Long submissionTechniqueId,
        @Size(max = 50) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String round,
        @Size(max = 255) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String techniqueText,
        @Size(max = 10000) String notesMarkdown
) {}
