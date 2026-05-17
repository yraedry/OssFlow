package com.ossflow.identity.profile.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
public record UserProfile(
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
        String ageCategory,
        boolean onboardingCompleted,
        List<UserProfileFederation> federations,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
