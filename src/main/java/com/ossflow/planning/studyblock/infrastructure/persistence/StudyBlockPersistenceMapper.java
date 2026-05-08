package com.ossflow.planning.studyblock.infrastructure.persistence;

import com.ossflow.planning.studyblock.domain.StudyBlock;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyBlockPersistenceMapper {

    StudyBlock toDomain(StudyBlockEntity entity);

    StudyBlockEntity toEntity(StudyBlock domain);

    void updateEntity(StudyBlock domain, @MappingTarget StudyBlockEntity entity);
}
