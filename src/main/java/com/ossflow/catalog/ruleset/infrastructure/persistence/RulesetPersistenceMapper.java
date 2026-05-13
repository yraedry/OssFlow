package com.ossflow.catalog.ruleset.infrastructure.persistence;

import com.ossflow.catalog.ruleset.domain.Ruleset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = RulesetTechniqueMapper.class)
public interface RulesetPersistenceMapper {

    @Mapping(target = "techniques", source = "techniques")
    @Mapping(target = "federationName", expression = "java(entity.getFederation() != null ? entity.getFederation().getName() : null)")
    Ruleset toDomain(RulesetEntity entity);

    @Mapping(target = "techniques", ignore = true)
    @Mapping(target = "federation", ignore = true)
    RulesetEntity toEntity(Ruleset domain);
}
