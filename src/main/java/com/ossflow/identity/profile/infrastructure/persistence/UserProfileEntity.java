package com.ossflow.identity.profile.infrastructure.persistence;

import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

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

    @Column(name = "current_belt", nullable = false, length = 15)
    private String currentBelt;

    @Column(name = "belt_since", columnDefinition = "date")
    private LocalDate beltSince;

    @Column(name = "academy", length = 200)
    private String academy;

    @Column(name = "preferred_modality", nullable = false, length = 10)
    private String preferredModality;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserProfileFederationEntity> federations = new ArrayList<>();
}
