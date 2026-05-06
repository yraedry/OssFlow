package com.ossflow.planning.studyblock.infrastructure.web;

import com.ossflow.planning.studyblock.domain.StudyBlock;
import com.ossflow.planning.studyblock.infrastructure.web.dto.CreateStudyBlockRequest;
import com.ossflow.planning.studyblock.infrastructure.web.dto.StudyBlockResponse;
import com.ossflow.planning.studyblock.infrastructure.web.dto.UpdateStudyBlockRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyBlockWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studyPlanId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudyBlock fromCreate(CreateStudyBlockRequest req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studyPlanId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudyBlock fromUpdate(UpdateStudyBlockRequest req);

    StudyBlockResponse toResponse(StudyBlock studyBlock);
}
