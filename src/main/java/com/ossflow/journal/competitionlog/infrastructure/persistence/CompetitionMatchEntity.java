package com.ossflow.journal.competitionlog.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "competition_match")
@Getter
@Setter
public class CompetitionMatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_order")
    private Integer matchOrder;

    @Column(name = "opponent_name", length = 200)
    private String opponentName;

    @Column(name = "opponent_team", length = 200)
    private String opponentTeam;

    @Column(name = "outcome", length = 20)
    private String outcome;

    @Column(name = "method", length = 50)
    private String method;

    @Column(name = "submission_technique_id")
    private Long submissionTechniqueId;

    @Column(name = "notes_markdown", columnDefinition = "TEXT")
    private String notesMarkdown;

    @Column(name = "round", length = 50)
    private String round;

    @Column(name = "technique_text", length = 255)
    private String techniqueText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_log_id", nullable = false)
    private CompetitionLogEntity competitionLog;
}
