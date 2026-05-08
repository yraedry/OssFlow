package com.ossflow.planning.studyitem.infrastructure.persistence;

import com.ossflow.planning.studyitem.domain.StudyItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyItemPersistenceMapper {

    StudyItem toDomain(StudyItemEntity entity);

    StudyItemEntity toEntity(StudyItem domain);

    void updateEntity(StudyItem domain, @MappingTarget StudyItemEntity entity);
}
