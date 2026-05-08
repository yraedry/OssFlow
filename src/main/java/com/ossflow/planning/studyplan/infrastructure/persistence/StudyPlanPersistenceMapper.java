package com.ossflow.planning.studyplan.infrastructure.persistence;

import com.ossflow.planning.studyplan.domain.StudyPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyPlanPersistenceMapper {

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    @Mapping(target = "deletedAt", expression = "java(entity.getDeletedAtInstant())")
    @Mapping(target = "purgeAt", expression = "java(entity.getPurgeAtInstant())")
    StudyPlan toDomain(StudyPlanEntity entity);

    StudyPlanEntity toEntity(StudyPlan domain);

    void updateEntity(StudyPlan domain, @MappingTarget StudyPlanEntity entity);
}
