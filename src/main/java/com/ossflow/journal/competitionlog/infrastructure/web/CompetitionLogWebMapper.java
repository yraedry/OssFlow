package com.ossflow.journal.competitionlog.infrastructure.web;

import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import com.ossflow.journal.competitionlog.domain.CompetitionMatch;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.AddCompetitionMatchRequest;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.CompetitionLogResponse;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.CompetitionMatchResponse;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.CreateCompetitionLogRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompetitionLogWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "matches", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    CompetitionLog fromCreate(CreateCompetitionLogRequest req);

    CompetitionLogResponse toResponse(CompetitionLog log);

    CompetitionMatchResponse toMatchResponse(CompetitionMatch match);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "competitionLogId", ignore = true)
    CompetitionMatch fromMatchRequest(AddCompetitionMatchRequest req);
}
