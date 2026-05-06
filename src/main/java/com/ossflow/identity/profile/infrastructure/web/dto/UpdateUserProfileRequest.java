package com.ossflow.identity.profile.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserProfileRequest(
        @Size(max = 120) String displayName,
        @Size(max = 15) String currentBelt,
        LocalDate beltSince,
        @Size(max = 200) String academy,
        @Size(max = 10) String preferredModality
) {}
