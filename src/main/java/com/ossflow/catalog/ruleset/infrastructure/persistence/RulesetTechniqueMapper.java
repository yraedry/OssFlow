package com.ossflow.catalog.ruleset.infrastructure.persistence;

import com.ossflow.catalog.ruleset.domain.RulesetTechnique;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RulesetTechniqueMapper {

    @Mapping(target = "rulesetId", source = "id.rulesetId")
    @Mapping(target = "techniqueId", source = "id.techniqueId")
    RulesetTechnique toDomain(RulesetTechniqueEntity entity);
}
