package com.ossflow.catalog.position.infrastructure.web;

import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.infrastructure.web.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    Position fromCreate(CreatePositionRequest req);

    PositionResponse toResponse(Position position);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Position applyPatch(PatchPositionRequest req, @MappingTarget Position position);
}
