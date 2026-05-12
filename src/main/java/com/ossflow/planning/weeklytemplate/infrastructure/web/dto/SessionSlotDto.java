package com.ossflow.planning.weeklytemplate.infrastructure.web.dto;

import com.ossflow.planning.weeklytemplate.domain.SessionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SessionSlotDto(
        @NotNull SessionType type,
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido, use HH:mm")
        String time
) {}
