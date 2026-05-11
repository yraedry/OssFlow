package com.ossflow.identity.profile.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateUserProfileRequest(
        @NotBlank @Size(max = 120) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String displayName,
        @Size(max = 80) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String firstName,
        @Size(max = 80) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String lastName,
        @Size(max = 60) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String alias,
        @NotBlank @Size(max = 15) String currentBelt,
        LocalDate beltSince,
        @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String academy,
        @NotBlank @Size(max = 10) String preferredModality
) {}
