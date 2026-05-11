package com.ossflow.planning.studyblock.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateStudyBlockRequest(
        @NotBlank @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String title,
        LocalDate startDate,
        LocalDate endDate,
        @NotNull int blockOrder,
        @Size(max = 10000) String notesMarkdown,
        @Size(max = 2000) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String focusEntities
) {}
