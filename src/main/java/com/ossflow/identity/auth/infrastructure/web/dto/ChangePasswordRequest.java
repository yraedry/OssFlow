package com.ossflow.identity.auth.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank @Size(min = 8) String currentPassword,
    @NotBlank @Size(min = 8)
    @Pattern(regexp = ".*[A-Z].*", message = "Debe contener al menos una mayúscula")
    @Pattern(regexp = ".*[0-9].*", message = "Debe contener al menos un número")
    String newPassword
) {}
