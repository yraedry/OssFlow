package com.ossflow.planning.studyitem.application;

import com.ossflow.planning.studyitem.domain.StudyItemStatus;

import java.time.Instant;

public record StudyItemStatusChangedEvent(
        Long studyItemId,
        Long ownerId,
        StudyItemStatus from,
        StudyItemStatus to,
        Instant when
) {}
