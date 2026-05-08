package com.ossflow.identity.injury.infrastructure.web;

import com.ossflow.identity.injury.domain.Injury;
import com.ossflow.identity.injury.infrastructure.web.dto.CreateInjuryRequest;
import com.ossflow.identity.injury.infrastructure.web.dto.InjuryResponse;
import com.ossflow.identity.injury.infrastructure.web.dto.UpdateInjuryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InjuryWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Injury fromCreate(CreateInjuryRequest req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Injury fromUpdate(UpdateInjuryRequest req);

    InjuryResponse toResponse(Injury injury);
}
