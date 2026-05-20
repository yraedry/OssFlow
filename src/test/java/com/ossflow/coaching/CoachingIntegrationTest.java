package com.ossflow.coaching;

import com.ossflow.coaching.invitation.application.CoachInvitationService;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import com.ossflow.coaching.relationship.application.CoachAthleteService;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.AccountRole;
import com.ossflow.identity.auth.infrastructure.persistence.AccountEntity;
import com.ossflow.identity.auth.infrastructure.persistence.AccountJpaRepository;
import com.ossflow.shared.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CoachingIntegrationTest {

    @Autowired CoachInvitationService invitationService;
    @Autowired CoachAthleteService coachAthleteService;
    @Autowired AccountJpaRepository accountRepo;

    private Long coachId;
    private Long athleteId;

    @BeforeEach
    void createAccounts() {
        AccountEntity coach = accountRepo.save(AccountEntity.builder()
                .email("coach@test.local")
                .passwordHash("$2a$10$dummy-hash-for-test")
                .provider(AccountProvider.LOCAL)
                .emailVerified(true)
                .tokenVersion(0)
                .role(AccountRole.ATHLETE_COACH)
                .build());

        AccountEntity athlete = accountRepo.save(AccountEntity.builder()
                .email("athlete@test.local")
                .passwordHash("$2a$10$dummy-hash-for-test")
                .provider(AccountProvider.LOCAL)
                .emailVerified(true)
                .tokenVersion(0)
                .role(AccountRole.ATHLETE)
                .build());

        coachId = coach.getId();
        athleteId = athlete.getId();
    }

    @Test
    void full_invite_and_link_flow() {
        var inv = invitationService.generate(coachId);
        assertThat(inv.code()).hasSize(6);
        assertThat(inv.status()).isEqualTo(InvitationStatus.PENDING);

        var validated = invitationService.validateCode(inv.code());
        assertThat(validated).isNotNull();

        coachAthleteService.link(coachId, athleteId, inv.id());
        assertThat(coachAthleteService.isLinked(coachId, athleteId)).isTrue();

        coachAthleteService.unlinkByCoach(coachId, athleteId);
        assertThat(coachAthleteService.isLinked(coachId, athleteId)).isFalse();
    }

    @Test
    void generate_revokes_previous_invitation() {
        var first = invitationService.generate(coachId);
        var second = invitationService.generate(coachId);

        assertThat(second.status()).isEqualTo(InvitationStatus.PENDING);
        assertThat(invitationService.validateCode(first.code())).isNull();
    }

    @Test
    void link_throws_conflict_when_already_linked() {
        coachAthleteService.link(coachId, athleteId, null);

        assertThatThrownBy(() -> coachAthleteService.link(coachId, athleteId, null))
                .isInstanceOf(ConflictException.class);
    }
}
