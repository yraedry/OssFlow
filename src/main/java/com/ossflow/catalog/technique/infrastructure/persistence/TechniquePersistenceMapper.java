package com.ossflow.catalog.technique.infrastructure.persistence;

import com.ossflow.catalog.technique.domain.Technique;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TechniquePersistenceMapper {

    Technique toDomain(TechniqueEntity entity);

    TechniqueEntity toEntity(Technique domain);

    void updateEntity(Technique domain, @MappingTarget TechniqueEntity entity);
}
