package com.ossflow.coaching.recommendation.infrastructure.persistence;

import com.ossflow.coaching.recommendation.domain.TechniqueRecommendation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    TechniqueRecommendation toDomain(RecommendationEntity entity);
    RecommendationEntity toEntity(TechniqueRecommendation rec);
}
