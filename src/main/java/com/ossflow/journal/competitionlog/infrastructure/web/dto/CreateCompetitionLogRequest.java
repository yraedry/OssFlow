package com.ossflow.journal.competitionlog.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCompetitionLogRequest(
        @NotBlank @Size(max = 255) String eventName,
        @NotNull LocalDate eventDate,
        @Size(max = 50) String weightCategory,
        Integer totalMatches,
        @Size(max = 50) String result,
        @Size(max = 50000) String analysisMarkdown
) {}
