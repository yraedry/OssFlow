package com.ossflow.coaching.privatesession.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreatePrivateSessionRequest(
        @NotNull Long athleteId,
        Long gymId,
        @NotNull LocalDate sessionDate,
        LocalTime startTime,
        Integer durationMinutes,
        String title,
        String notes,
        List<String> techniquesWorked
) {}
