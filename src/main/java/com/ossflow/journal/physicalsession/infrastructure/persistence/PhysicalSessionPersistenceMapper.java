package com.ossflow.journal.physicalsession.infrastructure.persistence;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import org.springframework.stereotype.Component;

@Component
public class PhysicalSessionPersistenceMapper {

    public PhysicalSession toDomain(PhysicalSessionEntity e) {
        return PhysicalSession.builder()
                .id(e.getId())
                .ownerId(e.getOwnerId())
                .sessionDate(e.getSessionDate())
                .sessionType(e.getSessionType())
                .title(e.getTitle())
                .durationMinutes(e.getDurationMinutes())
                .notes(e.getNotes())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .version(e.getVersion())
                .deletedAt(e.getDeletedAt())
                .purgeAt(e.getPurgeAt())
                .build();
    }

    public PhysicalSessionEntity toEntity(PhysicalSession d) {
        PhysicalSessionEntity e = new PhysicalSessionEntity();
        if (d.id() != null) e.setId(d.id());
        e.setOwnerId(d.ownerId());
        e.setSessionDate(d.sessionDate());
        e.setSessionType(d.sessionType());
        e.setTitle(d.title());
        e.setDurationMinutes(d.durationMinutes());
        e.setNotes(d.notes());
        if (d.version() != null) e.setVersion(d.version());
        return e;
    }
}
