package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.domain.Position;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionPersistenceMapper {

    Position toDomain(PositionEntity entity);

    PositionEntity toEntity(Position domain);

    void updateEntity(Position domain, @MappingTarget PositionEntity entity);
}
