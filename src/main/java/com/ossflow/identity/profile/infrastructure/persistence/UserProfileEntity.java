package com.ossflow.identity.profile.infrastructure.persistence;

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
@Table(name = "user_profile")
@SQLRestriction("deleted_at IS NULL")
public class UserProfileEntity extends BaseEntity {

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "alias", length = 60, unique = true)
    private String alias;

    @Column(name = "current_belt", nullable = false, length = 15)
    private String currentBelt;

    @Column(name = "belt_since")
    @JdbcTypeCode(SqlTypes.DATE)
    private LocalDate beltSince;

    @Column(name = "academy", length = 200)
    private String academy;

    @Column(name = "preferred_modality", nullable = false, length = 10)
    private String preferredModality;

    @Column(name = "age_category", length = 20)
    private String ageCategory;

    @Column(name = "stripes")
    private Integer stripes;

    @Column(name = "weight", precision = 5, scale = 2)
    private Double weight;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserProfileFederationEntity> federations = new ArrayList<>();
}
