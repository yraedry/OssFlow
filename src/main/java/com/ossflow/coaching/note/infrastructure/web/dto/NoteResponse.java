package com.ossflow.coaching.note.infrastructure.web.dto;

import com.ossflow.coaching.note.domain.AthleteNote;

import java.time.Instant;

public record NoteResponse(
        Long id,
        Long coachId,
        Long athleteId,
        String body,
        String techniqueFamily,
        boolean deleted,
        boolean read,
        Instant createdAt
) {
    public static NoteResponse from(AthleteNote n) {
        return new NoteResponse(
                n.id(), n.coachId(), n.athleteId(), n.body(),
                n.techniqueFamily() != null ? n.techniqueFamily().name() : null,
                n.deletedAt() != null,
                n.readAt() != null,
                n.createdAt()
        );
    }
}
