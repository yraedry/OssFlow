package com.ossflow.journal.trainingsession.infrastructure.web;

import com.ossflow.journal.trainingsession.domain.TrainingSession;
import com.ossflow.journal.trainingsession.domain.WorkedTechnique;
import com.ossflow.journal.trainingsession.infrastructure.web.dto.CreateTrainingSessionRequest;
import com.ossflow.journal.trainingsession.infrastructure.web.dto.TrainingSessionResponse;
import com.ossflow.journal.trainingsession.infrastructure.web.dto.UpsertWorkedTechniqueRequest;
import com.ossflow.journal.trainingsession.infrastructure.web.dto.WorkedTechniqueResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TrainingSessionWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "workedTechniques", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    TrainingSession fromCreate(CreateTrainingSessionRequest req);

    TrainingSessionResponse toResponse(TrainingSession session);

    WorkedTechniqueResponse toWorkedTechniqueResponse(WorkedTechnique wt);

    @Mapping(target = "trainingSessionId", ignore = true)
    WorkedTechnique fromUpsertRequest(UpsertWorkedTechniqueRequest req);
}
