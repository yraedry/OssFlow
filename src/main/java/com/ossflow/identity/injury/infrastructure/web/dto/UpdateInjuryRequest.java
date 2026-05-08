package com.ossflow.identity.injury.infrastructure.web.dto;

import com.ossflow.identity.injury.domain.InjurySeverity;
import com.ossflow.identity.injury.domain.InjuryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateInjuryRequest(
        @NotBlank @Size(max = 100) String bodyPart,
        @Size(max = 10000) String description,
        @NotNull InjurySeverity severity,
        @NotNull InjuryStatus status,
        LocalDate startedOn,
        LocalDate recoveredOn
) {}
