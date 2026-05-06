package com.ossflow.journal.note.infrastructure.web;

import com.ossflow.journal.note.domain.Note;
import com.ossflow.journal.note.infrastructure.web.dto.CreateNoteRequest;
import com.ossflow.journal.note.infrastructure.web.dto.NoteResponse;
import com.ossflow.journal.note.infrastructure.web.dto.PatchNoteRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoteWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    Note fromCreate(CreateNoteRequest req);

    NoteResponse toResponse(Note note);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Note applyPatch(PatchNoteRequest req, @MappingTarget Note note);
}
