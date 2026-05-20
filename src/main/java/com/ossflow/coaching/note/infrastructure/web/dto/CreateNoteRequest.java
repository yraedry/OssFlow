package com.ossflow.coaching.note.infrastructure.web.dto;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(
        @NotNull Long athleteId,
        @NotBlank @Size(max = 2000) String body,
        TechniqueFamily techniqueFamily
) {}
