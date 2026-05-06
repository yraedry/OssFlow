package com.ossflow.identity.profile.infrastructure.web.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record UserProfileResponse(
        Long id,
        Long ownerId,
        String displayName,
        String currentBelt,
        LocalDate beltSince,
        String academy,
        String preferredModality,
        boolean onboardingCompleted,
        List<UserProfileFederationResponse> federations,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
