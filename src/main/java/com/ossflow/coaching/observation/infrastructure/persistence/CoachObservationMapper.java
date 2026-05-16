package com.ossflow.coaching.observation.infrastructure.persistence;

import com.ossflow.coaching.observation.domain.CoachObservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoachObservationMapper {
    CoachObservation toDomain(CoachObservationEntity entity);
    CoachObservationEntity toEntity(CoachObservation domain);
}
