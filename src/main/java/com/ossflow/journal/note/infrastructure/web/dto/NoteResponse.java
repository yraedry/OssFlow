package com.ossflow.journal.note.infrastructure.web.dto;

import java.time.Instant;
import java.util.List;

public record NoteResponse(
        Long id,
        String title,
        String bodyMarkdown,
        String targetType,
        Long targetId,
        List<String> tags,
        Instant createdAt,
        Instant updatedAt
) {}
