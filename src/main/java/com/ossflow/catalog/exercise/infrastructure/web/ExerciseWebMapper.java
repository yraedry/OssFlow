package com.ossflow.catalog.exercise.infrastructure.web;

import com.ossflow.catalog.exercise.domain.Exercise;
import com.ossflow.catalog.exercise.infrastructure.web.dto.CreateExerciseRequest;
import com.ossflow.catalog.exercise.infrastructure.web.dto.ExerciseResponse;
import com.ossflow.catalog.exercise.infrastructure.web.dto.UpdateExerciseRequest;
import com.ossflow.catalog.position.domain.Visibility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExerciseWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    @Mapping(target = "visibility", defaultExpression = "java(req.visibility() != null ? req.visibility() : Visibility.PRIVATE)")
    Exercise fromCreate(CreateExerciseRequest req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    @Mapping(target = "visibility", defaultExpression = "java(req.visibility() != null ? req.visibility() : Visibility.PRIVATE)")
    Exercise fromUpdate(UpdateExerciseRequest req);

    ExerciseResponse toResponse(Exercise exercise);
}
