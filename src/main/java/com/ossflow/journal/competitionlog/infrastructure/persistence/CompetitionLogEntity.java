package com.ossflow.journal.competitionlog.infrastructure.persistence;

import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "competition_log")
@SQLRestriction("deleted_at IS NULL")
public class CompetitionLogEntity extends BaseEntity {

    @Column(name = "event_name", nullable = false, length = 255)
    private String eventName;

    @Column(name = "event_date", nullable = false, columnDefinition = "date")
    @JdbcTypeCode(SqlTypes.DATE)
    private LocalDate eventDate;

    @Column(name = "weight_category", length = 50)
    private String weightCategory;

    @Column(name = "total_matches")
    private Integer totalMatches;

    @Column(name = "result", length = 50)
    private String result;

    @Column(name = "analysis_markdown", columnDefinition = "TEXT")
    private String analysisMarkdown;

    @Column(name = "category_age", length = 20)
    private String categoryAge;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "gi_nogi", length = 10)
    private String giNogi;

    @Column(name = "wins_count")
    private Integer winsCount;

    @Column(name = "losses_count")
    private Integer lossesCount;

    @OneToMany(mappedBy = "competitionLog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CompetitionMatchEntity> matches = new ArrayList<>();
}
