package com.ossflow.catalog.ruleset.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RulesetTechniqueJpaRepository extends JpaRepository<RulesetTechniqueEntity, RulesetTechniqueId> {
    List<RulesetTechniqueEntity> findByIdTechniqueId(Long techniqueId);
}
