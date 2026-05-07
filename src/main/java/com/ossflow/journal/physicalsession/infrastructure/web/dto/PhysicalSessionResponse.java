package com.ossflow.journal.physicalsession.infrastructure.web.dto;

import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;

import java.time.Instant;
import java.time.LocalDate;

public record PhysicalSessionResponse(
        Long id,
        LocalDate sessionDate,
        PhysicalSessionType sessionType,
        String title,
        Integer durationMinutes,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
