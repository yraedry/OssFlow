package com.ossflow.journal.note.domain;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record Note(
        Long id,
        Long ownerId,
        String title,
        String bodyMarkdown,
        String targetType,
        Long targetId,
        List<String> tags,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
