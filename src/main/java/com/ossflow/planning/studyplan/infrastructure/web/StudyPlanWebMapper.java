package com.ossflow.planning.studyplan.infrastructure.web;

import com.ossflow.planning.studyplan.domain.StudyPlan;
import com.ossflow.planning.studyplan.infrastructure.web.dto.CreateStudyPlanRequest;
import com.ossflow.planning.studyplan.infrastructure.web.dto.StudyPlanResponse;
import com.ossflow.planning.studyplan.infrastructure.web.dto.UpdateStudyPlanRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyPlanWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    StudyPlan fromCreate(CreateStudyPlanRequest req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    StudyPlan fromUpdate(UpdateStudyPlanRequest req);

    StudyPlanResponse toResponse(StudyPlan studyPlan);
}
