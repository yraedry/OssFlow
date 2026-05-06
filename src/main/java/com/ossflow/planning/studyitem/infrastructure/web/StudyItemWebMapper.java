package com.ossflow.planning.studyitem.infrastructure.web;

import com.ossflow.planning.studyitem.domain.StudyItem;
import com.ossflow.planning.studyitem.domain.StudyItemStatus;
import com.ossflow.planning.studyitem.infrastructure.web.dto.CreateStudyItemRequest;
import com.ossflow.planning.studyitem.infrastructure.web.dto.StudyItemResponse;
import com.ossflow.planning.studyitem.infrastructure.web.dto.UpdateStudyItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = StudyItemStatus.class)
public interface StudyItemWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studyBlockId", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", expression = "java(req.status() != null ? req.status() : StudyItemStatus.TODO)")
    StudyItem fromCreate(CreateStudyItemRequest req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studyBlockId", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    StudyItem fromUpdate(UpdateStudyItemRequest req);

    StudyItemResponse toResponse(StudyItem studyItem);
}
