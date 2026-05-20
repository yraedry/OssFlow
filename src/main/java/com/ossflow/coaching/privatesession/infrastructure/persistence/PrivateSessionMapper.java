package com.ossflow.coaching.privatesession.infrastructure.persistence;

import com.ossflow.coaching.privatesession.domain.PrivateSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PrivateSessionMapper {
    PrivateSession toDomain(PrivateSessionEntity entity);
    PrivateSessionEntity toEntity(PrivateSession domain);
}
