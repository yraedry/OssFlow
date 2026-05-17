package com.ossflow.coaching.privatesession.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder(toBuilder = true)
public record PrivateSession(
        Long id,
        Long coachId,
        Long athleteId,
        Long gymId,
        LocalDate sessionDate,
        LocalTime startTime,
        Integer durationMinutes,
        String title,
        String notes,
        Instant createdAt
) {}
