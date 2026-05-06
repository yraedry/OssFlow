package com.ossflow.journal.tag.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTagRequest(
        @NotBlank @Size(max = 100) String name
) {}
