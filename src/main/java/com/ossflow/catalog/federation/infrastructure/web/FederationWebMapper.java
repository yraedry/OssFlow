package com.ossflow.catalog.federation.infrastructure.web;

import com.ossflow.catalog.federation.domain.Federation;
import com.ossflow.catalog.federation.infrastructure.web.dto.FederationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FederationWebMapper {
    FederationResponse toResponse(Federation federation);
}
