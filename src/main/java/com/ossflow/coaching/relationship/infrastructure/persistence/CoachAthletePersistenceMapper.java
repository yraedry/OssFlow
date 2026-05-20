package com.ossflow.coaching.relationship.infrastructure.persistence;

import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoachAthletePersistenceMapper {
    CoachAthleteRelationship toDomain(CoachAthleteEntity entity);
    CoachAthleteEntity toEntity(CoachAthleteRelationship domain);
}
