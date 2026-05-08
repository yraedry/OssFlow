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

    @OneToMany(mappedBy = "competitionLog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CompetitionMatchEntity> matches = new ArrayList<>();
}
