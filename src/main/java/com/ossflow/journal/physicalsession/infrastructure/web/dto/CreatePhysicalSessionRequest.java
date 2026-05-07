package com.ossflow.journal.physicalsession.infrastructure.web.dto;

import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePhysicalSessionRequest(
        @NotNull LocalDate sessionDate,
        @NotNull PhysicalSessionType sessionType,
        @NotBlank @Size(max = 200) String title,
        @Positive Integer durationMinutes,
        @Size(max = 5000) String notes
) {}
