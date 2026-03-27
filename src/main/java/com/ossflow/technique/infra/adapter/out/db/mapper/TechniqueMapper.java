package com.ossflow.technique.infra.adapter.out.db.mapper;

import com.ossflow.technique.domain.model.Position;
import com.ossflow.technique.domain.model.Technique;
import com.ossflow.technique.infra.adapter.out.db.entity.PositionEntity;
import com.ossflow.technique.infra.adapter.out.db.entity.TechniqueEntity;
import org.mapstruct.Mapper;

// El componentModel="spring" es vital para que le ponga @Component
@Mapper(componentModel = "spring")
public interface TechniqueMapper {

    TechniqueEntity toEntity(Technique domain);
    Technique toDomain(TechniqueEntity entity);

    // Añadimos estas dos líneas para que MapStruct sepa traducir las posiciones internas
    PositionEntity toPositionEntity(Position domain);
    Position toPositionDomain(PositionEntity entity);
}