package com.ossflow.journal.physicalsession.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record PhysicalSession(
        Long id,
        Long ownerId,
        LocalDate sessionDate,
        PhysicalSessionType sessionType,
        String title,
        Integer durationMinutes,
        String notes,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
