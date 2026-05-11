package com.ossflow.journal.tag.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTagRequest(
        @NotBlank @Size(max = 100) @Pattern(regexp = "^[^<>]*$", message = "No se permiten caracteres HTML") String name
) {}
