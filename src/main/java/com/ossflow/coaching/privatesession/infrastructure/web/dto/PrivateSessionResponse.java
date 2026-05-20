package com.ossflow.coaching.privatesession.infrastructure.web.dto;

import com.ossflow.coaching.privatesession.domain.PrivateSession;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PrivateSessionResponse(
        Long id,
        Long coachId,
        Long athleteId,
        Long gymId,
        LocalDate sessionDate,
        LocalTime startTime,
        Integer durationMinutes,
        String title,
        String notes,
        List<String> techniquesWorked,
        Instant createdAt
) {
    public static PrivateSessionResponse from(PrivateSession s) {
        return new PrivateSessionResponse(
                s.id(), s.coachId(), s.athleteId(), s.gymId(),
                s.sessionDate(), s.startTime(), s.durationMinutes(),
                s.title(), s.notes(),
                s.techniquesWorked() != null ? s.techniquesWorked() : List.of(),
                s.createdAt()
        );
    }
}
