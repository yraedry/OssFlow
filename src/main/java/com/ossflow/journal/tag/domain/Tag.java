package com.ossflow.journal.tag.domain;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Tag(
        Long id,
        String name,
        Instant createdAt
) {}
