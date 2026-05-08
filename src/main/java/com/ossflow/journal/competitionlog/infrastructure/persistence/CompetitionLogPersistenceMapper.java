package com.ossflow.journal.competitionlog.infrastructure.persistence;

import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import com.ossflow.shared.persistence.InstantOffsetDateTimeMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CompetitionMatchMapper.class, InstantOffsetDateTimeMapper.class})
public interface CompetitionLogPersistenceMapper {

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    @Mapping(target = "deletedAt", expression = "java(entity.getDeletedAtInstant())")
    @Mapping(target = "purgeAt", expression = "java(entity.getPurgeAtInstant())")
    CompetitionLog toDomain(CompetitionLogEntity entity);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "matches", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    CompetitionLogEntity toEntity(CompetitionLog domain);

    @Mapping(target = "matches", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    void updateEntity(CompetitionLog domain, @MappingTarget CompetitionLogEntity entity);
}
