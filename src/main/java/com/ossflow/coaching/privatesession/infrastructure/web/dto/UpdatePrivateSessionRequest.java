package com.ossflow.coaching.privatesession.infrastructure.web.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdatePrivateSessionRequest(
        Long gymId,
        LocalDate sessionDate,
        LocalTime startTime,
        Integer durationMinutes,
        String title,
        String notes
) {}
