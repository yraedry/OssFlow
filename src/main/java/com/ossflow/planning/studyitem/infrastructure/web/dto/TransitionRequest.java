package com.ossflow.planning.studyitem.infrastructure.web.dto;

import com.ossflow.planning.studyitem.domain.StudyItemStatus;
import jakarta.validation.constraints.NotNull;

public record TransitionRequest(
        @NotNull StudyItemStatus targetStatus
) {}
