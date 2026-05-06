package com.ossflow.journal.trainingsession.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpsertWorkedTechniqueRequest(
        @NotNull @Positive Long techniqueId,
        @Min(0) Integer repCount,
        @Size(max = 10000) String notesMarkdown
) {}
