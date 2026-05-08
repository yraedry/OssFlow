package com.ossflow.catalog.federation.infrastructure.persistence;

import com.ossflow.catalog.federation.domain.Federation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FederationPersistenceMapper {
    Federation toDomain(FederationEntity entity);

    FederationEntity toEntity(Federation domain);
}
