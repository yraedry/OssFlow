package com.ossflow.catalog.technique.infrastructure.web;

import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.infrastructure.web.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TechniqueWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    Technique fromCreate(CreateTechniqueRequest req);

    TechniqueResponse toResponse(Technique technique);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Technique applyPatch(PatchTechniqueRequest req, @MappingTarget Technique technique);
}
