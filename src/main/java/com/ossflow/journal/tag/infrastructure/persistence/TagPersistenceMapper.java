package com.ossflow.journal.tag.infrastructure.persistence;

import com.ossflow.journal.tag.domain.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagPersistenceMapper {

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    Tag toDomain(TagEntity entity);

    TagEntity toEntity(Tag domain);
}
