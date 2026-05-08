package com.ossflow.identity.injury.infrastructure.persistence;

import com.ossflow.identity.injury.domain.Injury;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InjuryPersistenceMapper {

    Injury toDomain(InjuryEntity entity);

    InjuryEntity toEntity(Injury domain);

    void updateEntity(Injury domain, @MappingTarget InjuryEntity entity);
}
