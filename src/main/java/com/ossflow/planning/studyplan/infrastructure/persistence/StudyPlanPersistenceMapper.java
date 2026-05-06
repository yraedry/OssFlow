package com.ossflow.planning.studyplan.infrastructure.persistence;

import com.ossflow.planning.studyplan.domain.StudyPlan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyPlanPersistenceMapper {

    StudyPlan toDomain(StudyPlanEntity entity);

    StudyPlanEntity toEntity(StudyPlan domain);

    void updateEntity(StudyPlan domain, @MappingTarget StudyPlanEntity entity);
}
