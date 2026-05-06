package com.ossflow.journal.competitionlog.infrastructure.persistence;

import com.ossflow.journal.competitionlog.domain.CompetitionMatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompetitionMatchMapper {

    @Mapping(target = "competitionLogId", source = "competitionLog.id")
    CompetitionMatch toDomain(CompetitionMatchEntity entity);

    @Mapping(target = "competitionLog", ignore = true)
    CompetitionMatchEntity toEntity(CompetitionMatch domain);
}
