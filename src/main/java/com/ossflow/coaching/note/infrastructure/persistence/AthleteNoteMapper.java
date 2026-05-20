package com.ossflow.coaching.note.infrastructure.persistence;

import com.ossflow.coaching.note.domain.AthleteNote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AthleteNoteMapper {
    AthleteNote toDomain(AthleteNoteEntity entity);
    AthleteNoteEntity toEntity(AthleteNote domain);
}
