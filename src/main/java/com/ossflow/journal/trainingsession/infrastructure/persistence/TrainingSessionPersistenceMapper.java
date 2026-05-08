package com.ossflow.journal.trainingsession.infrastructure.persistence;

import com.ossflow.journal.trainingsession.domain.TrainingSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {WorkedTechniqueMapper.class})
public interface TrainingSessionPersistenceMapper {

    @Mapping(target = "workedTechniques", ignore = true)
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    @Mapping(target = "deletedAt", expression = "java(entity.getDeletedAtInstant())")
    @Mapping(target = "purgeAt", expression = "java(entity.getPurgeAtInstant())")
    TrainingSession toDomain(TrainingSessionEntity entity);

    @Mapping(target = "workedTechniques", source = "workedTechniques")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    @Mapping(target = "deletedAt", expression = "java(entity.getDeletedAtInstant())")
    @Mapping(target = "purgeAt", expression = "java(entity.getPurgeAtInstant())")
    TrainingSession toDomainWithTechniques(TrainingSessionEntity entity);

    @Mapping(target = "workedTechniques", ignore = true)
    TrainingSessionEntity toEntity(TrainingSession domain);

    @Mapping(target = "workedTechniques", ignore = true)
    void updateEntity(TrainingSession domain, @MappingTarget TrainingSessionEntity entity);
}
