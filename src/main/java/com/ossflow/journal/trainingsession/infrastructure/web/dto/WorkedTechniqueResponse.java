package com.ossflow.journal.trainingsession.infrastructure.web.dto;

public record WorkedTechniqueResponse(
        Long trainingSessionId,
        Long techniqueId,
        Integer repCount,
        String notesMarkdown
) {}
