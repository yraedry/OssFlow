package com.ossflow.catalog.ruleset.application.port;

import com.ossflow.catalog.ruleset.domain.Ruleset;
import com.ossflow.catalog.ruleset.domain.RulesetTechnique;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RulesetRepositoryPort {
    Ruleset save(Ruleset ruleset);
    Optional<Ruleset> findById(Long id);
    Page<Ruleset> findByFilters(Long federationId, Belt belt, Modality modality, Pageable pageable);
    boolean existsByUniqueKey(Long federationId, Belt belt, Modality modality, java.time.LocalDate effectiveFrom);
    RulesetTechnique upsertTechnique(Long rulesetId, RulesetTechnique technique);
    void removeTechnique(Long rulesetId, Long techniqueId);
    List<RulesetTechnique> findTechniquesByTechniqueId(Long techniqueId);
}
