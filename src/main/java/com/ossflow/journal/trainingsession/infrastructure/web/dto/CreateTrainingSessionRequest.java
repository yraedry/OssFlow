package com.ossflow.journal.trainingsession.infrastructure.web.dto;

import com.ossflow.journal.trainingsession.domain.Intensity;
import com.ossflow.journal.trainingsession.domain.SessionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTrainingSessionRequest(
        @NotNull @Past LocalDate sessionDate,
        @Min(1) int durationMinutes,
        @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String location,
        @NotNull Intensity intensity,
        @NotNull SessionType sessionType,
        @Size(max = 50000) String notesMarkdown
) {}
