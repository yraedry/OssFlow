package com.ossflow.journal.note.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateNoteRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 50000) String bodyMarkdown,
        @Size(max = 50) String targetType,
        Long targetId,
        List<@NotBlank @Size(max = 100) String> tags
) {}
