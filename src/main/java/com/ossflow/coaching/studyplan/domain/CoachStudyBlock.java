package com.ossflow.coaching.studyplan.domain;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record CoachStudyBlock(
        Long id,
        Long planId,
        Long classPlanId,
        String title,
        int blockOrder,
        List<CoachStudyItem> items
) {}
