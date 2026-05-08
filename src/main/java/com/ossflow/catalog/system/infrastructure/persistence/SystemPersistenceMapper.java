package com.ossflow.catalog.system.infrastructure.persistence;

import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.shared.persistence.InstantOffsetDateTimeMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = InstantOffsetDateTimeMapper.class)
public interface SystemPersistenceMapper {
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    @Mapping(target = "deletedAt", expression = "java(entity.getDeletedAtInstant())")
    @Mapping(target = "purgeAt", expression = "java(entity.getPurgeAtInstant())")
    OssSystem toDomain(SystemEntity entity);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    SystemEntity toEntity(OssSystem domain);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    void updateEntity(OssSystem domain, @MappingTarget SystemEntity entity);
}
