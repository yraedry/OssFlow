package com.ossflow.coaching.studyplan.domain;

import lombok.Builder;

@Builder(toBuilder = true)
public record CoachStudyItem(
        Long id,
        Long blockId,
        int itemOrder,
        StudyItemType itemType,
        String content,
        Long techniqueId,
        String techniqueName
) {}
