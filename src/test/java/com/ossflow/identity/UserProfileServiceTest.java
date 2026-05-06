package com.ossflow.identity;

import com.ossflow.identity.profile.application.UserProfileService;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.profile.domain.UserProfileFederation;
import com.ossflow.shared.exception.ConflictException;
import com.ossflow.shared.exception.UnprocessableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    UserProfileRepositoryPort repo;

    @InjectMocks
    UserProfileService service;

    private UserProfile minimalProfile(Long ownerId) {
        return UserProfile.builder()
                .ownerId(ownerId)
                .displayName("Test User")
                .currentBelt("BLUE")
                .preferredModality("GI")
                .onboardingCompleted(false)
                .build();
    }

    @Test
    void replaceFederations_zeroPrimaries_throwsUnprocessable() {
        Long ownerId = 1L;

        List<UserProfileFederation> federations = List.of(
                UserProfileFederation.builder().federationId(1L).isPrimary(false).build(),
                UserProfileFederation.builder().federationId(2L).isPrimary(false).build()
        );

        assertThatThrownBy(() -> service.replaceFederations(ownerId, federations))
                .isInstanceOf(UnprocessableException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PRIMARY_FEDERATION_REQUIRED");
    }

    @Test
    void replaceFederations_twoPrimaries_throwsUnprocessable() {
        Long ownerId = 1L;

        List<UserProfileFederation> federations = List.of(
                UserProfileFederation.builder().federationId(1L).isPrimary(true).build(),
                UserProfileFederation.builder().federationId(2L).isPrimary(true).build()
        );

        assertThatThrownBy(() -> service.replaceFederations(ownerId, federations))
                .isInstanceOf(UnprocessableException.class)
                .hasFieldOrPropertyWithValue("errorCode", "MULTIPLE_PRIMARY_FEDERATIONS");
    }

    @Test
    void replaceFederations_onePrimary_savesSuccessfully() {
        Long ownerId = 1L;
        UserProfile profile = minimalProfile(ownerId).toBuilder().id(1L).build();
        given(repo.findByOwnerId(ownerId)).willReturn(Optional.of(profile));
        given(repo.save(any())).willAnswer(inv -> inv.getArgument(0));

        List<UserProfileFederation> federations = List.of(
                UserProfileFederation.builder().federationId(1L).isPrimary(true).build(),
                UserProfileFederation.builder().federationId(2L).isPrimary(false).build()
        );

        service.replaceFederations(ownerId, federations);

        verify(repo).save(any(UserProfile.class));
    }

    @Test
    void createProfile_alreadyExists_throwsConflict() {
        UserProfile profile = minimalProfile(1L);
        given(repo.existsByOwnerId(1L)).willReturn(true);

        assertThatThrownBy(() -> service.createProfile(profile))
                .isInstanceOf(ConflictException.class)
                .hasFieldOrPropertyWithValue("errorCode", "PROFILE_ALREADY_EXISTS");
    }
}
