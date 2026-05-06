package com.ossflow.journal.trainingsession.infrastructure.persistence;

import com.ossflow.journal.trainingsession.domain.WorkedTechnique;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkedTechniqueMapper {

    @Mapping(target = "trainingSessionId", source = "id.trainingSessionId")
    @Mapping(target = "techniqueId", source = "id.techniqueId")
    WorkedTechnique toDomain(TrainingSessionTechniqueEntity entity);
}
