package com.ossflow.identity.profile.infrastructure.web.dto;

import com.ossflow.identity.auth.domain.AccountRole;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record UserProfileResponse(
        Long id,
        Long ownerId,
        String displayName,
        String firstName,
        String lastName,
        String alias,
        String currentBelt,
        LocalDate beltSince,
        String academy,
        String preferredModality,
        boolean onboardingCompleted,
        List<UserProfileFederationResponse> federations,
        AccountRole role,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
