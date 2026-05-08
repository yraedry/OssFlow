package com.ossflow.catalog.exercise.infrastructure.persistence;

import com.ossflow.catalog.exercise.domain.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExercisePersistenceMapper {

    Exercise toDomain(ExerciseEntity entity);

    ExerciseEntity toEntity(Exercise domain);

    void updateEntity(Exercise domain, @MappingTarget ExerciseEntity entity);
}
