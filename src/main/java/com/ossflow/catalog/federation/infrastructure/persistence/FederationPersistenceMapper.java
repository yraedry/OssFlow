package com.ossflow.catalog.federation.infrastructure.persistence;

import com.ossflow.catalog.federation.domain.Federation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FederationPersistenceMapper {
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    Federation toDomain(FederationEntity entity);
    FederationEntity toEntity(Federation domain);
}
