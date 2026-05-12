package com.ossflow.planning.studyblock.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateStudyBlockRequest(
        @NotBlank @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String title,
        LocalDate startDate,
        LocalDate endDate,
        @JsonSetter(nulls = Nulls.SKIP) int blockOrder,
        @Size(max = 10000) String notesMarkdown,
        @Size(max = 2000) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String focusEntities
) {}
