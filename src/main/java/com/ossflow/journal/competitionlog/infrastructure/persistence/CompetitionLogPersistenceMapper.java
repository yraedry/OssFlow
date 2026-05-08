package com.ossflow.journal.competitionlog.infrastructure.persistence;

import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CompetitionMatchMapper.class})
public interface CompetitionLogPersistenceMapper {

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    @Mapping(target = "deletedAt", expression = "java(entity.getDeletedAtInstant())")
    @Mapping(target = "purgeAt", expression = "java(entity.getPurgeAtInstant())")
    CompetitionLog toDomain(CompetitionLogEntity entity);

    @Mapping(target = "matches", ignore = true)
    CompetitionLogEntity toEntity(CompetitionLog domain);

    @Mapping(target = "matches", ignore = true)
    void updateEntity(CompetitionLog domain, @MappingTarget CompetitionLogEntity entity);
}
