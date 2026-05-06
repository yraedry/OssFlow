package com.ossflow.journal.tag.infrastructure.persistence;

import com.ossflow.journal.tag.domain.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagPersistenceMapper {

    Tag toDomain(TagEntity entity);

    TagEntity toEntity(Tag domain);
}
