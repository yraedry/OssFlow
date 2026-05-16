package com.ossflow.coaching.note.infrastructure.web.dto;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNoteRequest(
        @NotNull Long athleteId,
        @NotBlank String body,
        TechniqueFamily techniqueFamily
) {}
