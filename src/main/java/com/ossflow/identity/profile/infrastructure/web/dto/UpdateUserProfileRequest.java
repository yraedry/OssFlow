package com.ossflow.identity.profile.infrastructure.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * No HTML ni etiquetas especiales en campos de texto de perfil.
 * El patrón niega los caracteres < > que son el vector principal de XSS almacenado.
 */
public record UpdateUserProfileRequest(
        @Size(max = 120) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String displayName,
        @Size(max = 80) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String firstName,
        @Size(max = 80) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String lastName,
        @Size(max = 60) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String alias,
        @Size(max = 15) String currentBelt,
        LocalDate beltSince,
        @Size(max = 200) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String academy,
        @Size(max = 10) String preferredModality,
        @Size(max = 20) String ageCategory
) {}
