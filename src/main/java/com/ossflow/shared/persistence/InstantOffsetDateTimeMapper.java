package com.ossflow.shared.persistence;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface InstantOffsetDateTimeMapper {

    default OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

    default Instant toInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toInstant() : null;
    }
}
