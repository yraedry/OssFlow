package com.ossflow.catalog.ruleset.infrastructure.persistence;

import com.ossflow.catalog.ruleset.domain.LegalityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ruleset_technique")
@Getter
@Setter
public class RulesetTechniqueEntity {

    @EmbeddedId
    private RulesetTechniqueId id = new RulesetTechniqueId();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LegalityStatus status;

    @Column(name = "condition_notes", columnDefinition = "TEXT")
    private String conditionNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rulesetId")
    @JoinColumn(name = "ruleset_id")
    private RulesetEntity ruleset;
}
