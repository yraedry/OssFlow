package com.ossflow.catalog.system.infrastructure.web;

import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.catalog.system.infrastructure.web.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SystemWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "flowSchemaVersion", constant = "v1")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    OssSystem fromCreate(CreateSystemRequest req);

    SystemResponse toResponse(OssSystem system);
}
