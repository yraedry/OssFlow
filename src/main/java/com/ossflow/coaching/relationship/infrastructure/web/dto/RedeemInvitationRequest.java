package com.ossflow.coaching.relationship.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedeemInvitationRequest(@NotBlank @Size(min = 6, max = 6) String code) {}
