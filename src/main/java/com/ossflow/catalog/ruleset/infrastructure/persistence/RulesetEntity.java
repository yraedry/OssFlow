package com.ossflow.catalog.ruleset.infrastructure.persistence;

import com.ossflow.catalog.federation.infrastructure.persistence.FederationEntity;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ruleset")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class RulesetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "federation_id", nullable = false)
    private Long federationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "federation_id", insertable = false, updatable = false)
    private FederationEntity federation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Belt belt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Modality modality;

    @Column(name = "effective_from", nullable = false, columnDefinition = "date")
    @JdbcTypeCode(SqlTypes.DATE)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to", columnDefinition = "date")
    @JdbcTypeCode(SqlTypes.DATE)
    private LocalDate effectiveTo;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @OneToMany(mappedBy = "ruleset", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RulesetTechniqueEntity> techniques = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;
}
