package com.ossflow.identity.injury.infrastructure.persistence;

import com.ossflow.identity.injury.domain.Injury;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InjuryPersistenceMapper {

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    Injury toDomain(InjuryEntity entity);

    InjuryEntity toEntity(Injury domain);

    void updateEntity(Injury domain, @MappingTarget InjuryEntity entity);
}
