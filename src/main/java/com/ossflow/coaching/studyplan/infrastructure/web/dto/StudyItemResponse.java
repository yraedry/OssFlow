package com.ossflow.coaching.studyplan.infrastructure.web.dto;

public record StudyItemResponse(
        Long id,
        int itemOrder,
        String itemType,
        String content,
        Long techniqueId,
        String techniqueName
) {}
