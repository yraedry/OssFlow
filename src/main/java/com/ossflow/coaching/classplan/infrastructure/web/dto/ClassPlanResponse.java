package com.ossflow.coaching.classplan.infrastructure.web.dto;

import com.ossflow.coaching.classplan.domain.ClassPlan;
import com.ossflow.coaching.studyplan.infrastructure.web.dto.StudyBlockResponse;
import com.ossflow.coaching.studyplan.infrastructure.web.dto.StudyItemResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ClassPlanResponse(
        Long id,
        Long gymId,
        String title,
        String description,
        LocalDate scheduledDate,
        Integer durationMinutes,
        String modality,
        String status,
        List<StudyBlockResponse> blocks,
        Instant createdAt,
        Instant updatedAt
) {
    public static ClassPlanResponse from(ClassPlan p) {
        var blocks = p.blocks() != null
                ? p.blocks().stream().map(b -> {
                    var items = b.items() != null
                            ? b.items().stream().map(i -> new StudyItemResponse(
                                i.id(), i.itemOrder(),
                                i.itemType() != null ? i.itemType().name() : null,
                                i.content(), i.techniqueId(), i.techniqueName())).toList()
                            : List.<StudyItemResponse>of();
                    return new StudyBlockResponse(b.id(), b.title(), b.blockOrder(), items);
                }).toList()
                : List.<StudyBlockResponse>of();
        return new ClassPlanResponse(p.id(), p.gymId(), p.title(), p.description(),
                p.scheduledDate(), p.durationMinutes(), p.modality(),
                p.status() != null ? p.status().name() : null,
                blocks, p.createdAt(), p.updatedAt());
    }
}
