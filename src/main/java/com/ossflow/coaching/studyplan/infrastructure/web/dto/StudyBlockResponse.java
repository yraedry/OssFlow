package com.ossflow.coaching.studyplan.infrastructure.web.dto;

import java.util.List;

public record StudyBlockResponse(
        Long id,
        String title,
        int blockOrder,
        List<StudyItemResponse> items
) {}
