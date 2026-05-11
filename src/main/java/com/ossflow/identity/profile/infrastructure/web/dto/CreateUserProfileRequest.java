package com.ossflow.identity.profile.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateUserProfileRequest(
        @NotBlank @Size(max = 120) String displayName,
        @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,
        @Size(max = 60) String alias,
        @NotBlank @Size(max = 15) String currentBelt,
        LocalDate beltSince,
        @Size(max = 200) String academy,
        @NotBlank @Size(max = 10) String preferredModality
) {}
