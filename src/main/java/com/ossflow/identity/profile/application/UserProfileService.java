package com.ossflow.identity.profile.application;

import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.profile.domain.UserProfileFederation;
import com.ossflow.shared.exception.ConflictException;
import com.ossflow.shared.exception.NotFoundException;
import com.ossflow.shared.exception.UnprocessableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepositoryPort repository;

    public UserProfile createProfile(UserProfile profile) {
        if (repository.existsByOwnerId(profile.ownerId())) {
            throw new ConflictException("PROFILE_ALREADY_EXISTS",
                    "Ya existe un perfil para este usuario",
                    Map.of("ownerId", profile.ownerId()));
        }
        UserProfile toSave = profile.toBuilder().onboardingCompleted(true).build();
        UserProfile saved = repository.save(toSave);
        log.info("UserProfile creado id={} ownerId={}", saved.id(), saved.ownerId());
        return saved;
    }

    public UserProfile getProfileByOwner(Long ownerId) {
        return repository.findByOwnerId(ownerId)
                .orElseThrow(() -> new NotFoundException("PROFILE_NOT_FOUND",
                        "No existe perfil para el usuario con id %d".formatted(ownerId),
                        Map.of("ownerId", ownerId)));
    }

    public UserProfile updateProfile(Long ownerId, UserProfile patch) {
        UserProfile existing = getProfileByOwner(ownerId);
        if (patch.alias() != null && !patch.alias().equals(existing.alias())
                && repository.existsByAliasAndOwnerIdNot(patch.alias(), ownerId)) {
            throw new ConflictException("ALIAS_ALREADY_TAKEN",
                    "El alias '%s' ya está en uso".formatted(patch.alias()),
                    Map.of("alias", patch.alias()));
        }
        UserProfile toSave = existing.toBuilder()
                .firstName(patch.firstName() != null ? patch.firstName() : existing.firstName())
                .lastName(patch.lastName() != null ? patch.lastName() : existing.lastName())
                .alias(patch.alias() != null ? patch.alias() : existing.alias())
                .displayName(patch.displayName() != null ? patch.displayName() : existing.displayName())
                .currentBelt(patch.currentBelt() != null ? patch.currentBelt() : existing.currentBelt())
                .beltSince(patch.beltSince() != null ? patch.beltSince() : existing.beltSince())
                .academy(patch.academy() != null ? patch.academy() : existing.academy())
                .preferredModality(patch.preferredModality() != null ? patch.preferredModality() : existing.preferredModality())
                .build();
        return repository.save(toSave);
    }

    public UserProfile replaceFederations(Long ownerId, List<UserProfileFederation> federations) {
        long primaryCount = federations.stream().filter(UserProfileFederation::isPrimary).count();
        if (primaryCount == 0) {
            throw new UnprocessableException("PRIMARY_FEDERATION_REQUIRED",
                    "Se requiere exactamente una federación primaria",
                    Map.of());
        }
        if (primaryCount > 1) {
            throw new UnprocessableException("MULTIPLE_PRIMARY_FEDERATIONS",
                    "Solo puede haber una federación primaria",
                    Map.of());
        }
        UserProfile existing = getProfileByOwner(ownerId);
        UserProfile toSave = existing.toBuilder().federations(federations).build();
        return repository.save(toSave);
    }
}
