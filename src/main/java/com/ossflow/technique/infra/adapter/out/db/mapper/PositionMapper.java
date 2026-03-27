package com.ossflow.technique.infra.adapter.out.db.mapper;

import com.ossflow.technique.domain.model.Position;
import com.ossflow.technique.infra.adapter.out.db.entity.PositionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    PositionEntity toEntity(Position domain);
    Position toDomain(PositionEntity entity);
}