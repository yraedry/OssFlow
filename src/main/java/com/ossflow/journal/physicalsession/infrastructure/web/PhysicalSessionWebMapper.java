package com.ossflow.journal.physicalsession.infrastructure.web;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.CreatePhysicalSessionRequest;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.PhysicalSessionResponse;
import org.springframework.stereotype.Component;

@Component
public class PhysicalSessionWebMapper {

    public PhysicalSession fromCreate(CreatePhysicalSessionRequest req) {
        return PhysicalSession.builder()
                .sessionDate(req.sessionDate())
                .sessionType(req.sessionType())
                .title(req.title())
                .durationMinutes(req.durationMinutes())
                .notes(req.notes())
                .build();
    }

    public PhysicalSessionResponse toResponse(PhysicalSession d) {
        return new PhysicalSessionResponse(
                d.id(),
                d.sessionDate(),
                d.sessionType(),
                d.title(),
                d.durationMinutes(),
                d.notes(),
                d.createdAt(),
                d.updatedAt()
        );
    }
}
