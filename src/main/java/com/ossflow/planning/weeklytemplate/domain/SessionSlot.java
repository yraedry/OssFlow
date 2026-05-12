package com.ossflow.planning.weeklytemplate.domain;

import lombok.Builder;

@Builder
public record SessionSlot(
        SessionType type,
        String time  // "HH:mm" opcional, null si no se especifica
) {}
