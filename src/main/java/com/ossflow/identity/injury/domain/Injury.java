package com.ossflow.identity.injury.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record Injury(
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
