package com.ossflow.journal.competitionlog.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCompetitionLogRequest(
        @NotBlank @Size(max = 255) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String eventName,
        @NotNull @PastOrPresent LocalDate eventDate,
        @Size(max = 50) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String weightCategory,
        @Size(max = 20) String categoryAge,
        @Size(max = 255) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String location,
        @Size(max = 10) String giNogi,
        Integer totalMatches,
        Integer winsCount,
        Integer lossesCount,
        @Size(max = 50) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String result,
        @Size(max = 50000) String analysisMarkdown
) {}
