package com.ossflow.journal.physicalsession.infrastructure.persistence;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhysicalSessionPersistenceMapper {

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    @Mapping(target = "deletedAt", expression = "java(entity.getDeletedAtInstant())")
    @Mapping(target = "purgeAt", expression = "java(entity.getPurgeAtInstant())")
    PhysicalSession toDomain(PhysicalSessionEntity entity);

    PhysicalSessionEntity toEntity(PhysicalSession domain);

    void updateEntity(PhysicalSession domain, @MappingTarget PhysicalSessionEntity entity);
}
