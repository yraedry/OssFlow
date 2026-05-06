package com.ossflow.catalog.ruleset.infrastructure.web;

import com.ossflow.catalog.ruleset.domain.Ruleset;
import com.ossflow.catalog.ruleset.domain.RulesetTechnique;
import com.ossflow.catalog.ruleset.infrastructure.web.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RulesetWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "techniques", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Ruleset fromCreate(CreateRulesetRequest req);

    RulesetResponse toResponse(Ruleset ruleset);

    @Mapping(target = "techniqueId", source = "techniqueId")
    RulesetTechniqueResponse toResponse(RulesetTechnique technique);

    @Mapping(target = "rulesetId", ignore = true)
    RulesetTechnique fromUpsert(UpsertRulesetTechniqueRequest req);
}
