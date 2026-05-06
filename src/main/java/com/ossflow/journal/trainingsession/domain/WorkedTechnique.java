package com.ossflow.journal.trainingsession.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record WorkedTechnique(
        Long trainingSessionId,
        Long techniqueId,
        Integer repCount,
        String notesMarkdown
) {}
