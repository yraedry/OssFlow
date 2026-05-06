package com.ossflow.identity.profile.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserProfileFederation(Long federationId, boolean isPrimary) {}
