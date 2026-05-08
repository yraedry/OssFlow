package com.ossflow.journal.trainingsession.infrastructure.persistence;

import com.ossflow.journal.trainingsession.domain.TrainingSession;
import com.ossflow.shared.persistence.InstantOffsetDateTimeMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {WorkedTechniqueMapper.class, InstantOffsetDateTimeMapper.class})
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

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "workedTechniques", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    TrainingSessionEntity toEntity(TrainingSession domain);

    @Mapping(target = "workedTechniques", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    void updateEntity(TrainingSession domain, @MappingTarget TrainingSessionEntity entity);
}
