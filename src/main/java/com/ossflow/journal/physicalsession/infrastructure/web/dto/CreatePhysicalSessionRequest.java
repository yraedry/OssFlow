package com.ossflow.journal.physicalsession.infrastructure.web.dto;

import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePhysicalSessionRequest(
        @NotNull @PastOrPresent LocalDate sessionDate,
        @NotNull PhysicalSessionType sessionType,
        @NotBlank @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String title,
        @Min(1) Integer durationMinutes,
        @Size(max = 5000) String notes
) {}
