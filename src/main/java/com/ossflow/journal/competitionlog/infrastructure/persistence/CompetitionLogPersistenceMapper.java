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

    CompetitionLog toDomain(CompetitionLogEntity entity);

    @Mapping(target = "matches", ignore = true)
    CompetitionLogEntity toEntity(CompetitionLog domain);

    @Mapping(target = "matches", ignore = true)
    void updateEntity(CompetitionLog domain, @MappingTarget CompetitionLogEntity entity);
}
