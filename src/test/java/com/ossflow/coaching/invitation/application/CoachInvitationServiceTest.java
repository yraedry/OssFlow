package com.ossflow.coaching.invitation.application;

import com.ossflow.coaching.invitation.application.port.CoachInvitationRepositoryPort;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachInvitationServiceTest {

    @Mock CoachInvitationRepositoryPort repo;
    @InjectMocks CoachInvitationService service;

    @Test
    void generate_creates_new_code_and_revokes_previous() {
        CoachInvitation existing = CoachInvitation.builder()
                .id(1L).coachId(10L).code("ABC123")
                .status(InvitationStatus.PENDING).usedCount(0)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now()).build();

        when(repo.findActiveByCoachId(10L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CoachInvitation result = service.generate(10L);

        assertThat(result.status()).isEqualTo(InvitationStatus.PENDING);
        assertThat(result.code()).hasSize(6);
        assertThat(result.coachId()).isEqualTo(10L);
        verify(repo).save(argThat(inv -> inv.status() == InvitationStatus.REVOKED && inv.id().equals(1L)));
        verify(repo).save(argThat(inv -> inv.status() == InvitationStatus.PENDING && inv.id() == null));
    }

    @Test
    void generate_without_previous_creates_new_code() {
        when(repo.findActiveByCoachId(10L)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CoachInvitation result = service.generate(10L);

        assertThat(result.code()).hasSize(6);
        verify(repo, times(1)).save(any());
    }

    @Test
    void revoke_sets_status_revoked() {
        CoachInvitation existing = CoachInvitation.builder()
                .id(1L).coachId(10L).code("ABC123")
                .status(InvitationStatus.PENDING).usedCount(0)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now()).build();

        when(repo.findActiveByCoachId(10L)).thenReturn(Optional.of(existing));

        service.revoke(10L);

        verify(repo).save(argThat(inv -> inv.status() == InvitationStatus.REVOKED));
    }

    @Test
    void validateCode_returns_null_when_expired() {
        CoachInvitation expired = CoachInvitation.builder()
                .id(1L).coachId(10L).code("XYZ789")
                .status(InvitationStatus.PENDING).usedCount(0)
                .expiresAt(Instant.now().minusSeconds(1))
                .createdAt(Instant.now().minusSeconds(3600)).build();

        when(repo.findByCode("XYZ789")).thenReturn(Optional.of(expired));

        CoachInvitation result = service.validateCode("XYZ789");

        assertThat(result).isNull();
    }

    @Test
    void validateCode_returns_invitation_when_valid() {
        CoachInvitation valid = CoachInvitation.builder()
                .id(1L).coachId(10L).code("VALID1")
                .status(InvitationStatus.PENDING).usedCount(2)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now()).build();

        when(repo.findByCode("VALID1")).thenReturn(Optional.of(valid));

        CoachInvitation result = service.validateCode("VALID1");

        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("VALID1");
    }
}
