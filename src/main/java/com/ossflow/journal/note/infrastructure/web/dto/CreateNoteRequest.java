package com.ossflow.journal.note.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateNoteRequest(
        @NotBlank @Size(max = 255) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String title,
        @Size(max = 50000) String bodyMarkdown,
        @Size(max = 50) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String targetType,
        Long targetId,
        List<@NotBlank @Size(max = 100) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String> tags
) {}
