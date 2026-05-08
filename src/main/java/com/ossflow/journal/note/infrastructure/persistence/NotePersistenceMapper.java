package com.ossflow.journal.note.infrastructure.persistence;

import com.ossflow.journal.note.domain.Note;
import com.ossflow.shared.persistence.InstantOffsetDateTimeMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = InstantOffsetDateTimeMapper.class)
public interface NotePersistenceMapper {

    @Mapping(target = "tags",
             expression = "java(entity.getTags().stream().map(t -> t.getName()).toList())")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    @Mapping(target = "deletedAt", expression = "java(entity.getDeletedAtInstant())")
    @Mapping(target = "purgeAt", expression = "java(entity.getPurgeAtInstant())")
    Note toDomain(NoteEntity entity);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    NoteEntity toEntity(Note domain);

    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    void updateEntity(Note domain, @MappingTarget NoteEntity entity);
}
