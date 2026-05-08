package com.ossflow.identity.injury.infrastructure.web.dto;

import com.ossflow.identity.injury.domain.InjurySeverity;
import com.ossflow.identity.injury.domain.InjuryStatus;

import java.time.Instant;
import java.time.LocalDate;

public record InjuryResponse(
        Long id,
        Long ownerId,
        String bodyPart,
        String description,
        InjurySeverity severity,
        InjuryStatus status,
        LocalDate startedOn,
        LocalDate recoveredOn,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
