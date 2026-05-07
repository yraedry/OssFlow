package com.ossflow.journal.physicalsession.infrastructure.web;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.CreatePhysicalSessionRequest;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.PhysicalSessionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhysicalSessionWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    PhysicalSession fromCreate(CreatePhysicalSessionRequest req);

    PhysicalSessionResponse toResponse(PhysicalSession session);
}
