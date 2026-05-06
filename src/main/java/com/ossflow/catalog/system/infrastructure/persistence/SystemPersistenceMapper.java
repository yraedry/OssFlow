package com.ossflow.catalog.system.infrastructure.persistence;

import com.ossflow.catalog.system.domain.OssSystem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SystemPersistenceMapper {
    OssSystem toDomain(SystemEntity entity);
    SystemEntity toEntity(OssSystem domain);
    void updateEntity(OssSystem domain, @MappingTarget SystemEntity entity);
}
