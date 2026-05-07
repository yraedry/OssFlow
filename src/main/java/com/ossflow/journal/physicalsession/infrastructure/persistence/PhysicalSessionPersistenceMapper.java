package com.ossflow.journal.physicalsession.infrastructure.persistence;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhysicalSessionPersistenceMapper {

    PhysicalSession toDomain(PhysicalSessionEntity entity);

    PhysicalSessionEntity toEntity(PhysicalSession domain);

    void updateEntity(PhysicalSession domain, @MappingTarget PhysicalSessionEntity entity);
}
