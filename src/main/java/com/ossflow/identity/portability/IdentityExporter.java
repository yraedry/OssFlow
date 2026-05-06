package com.ossflow.identity.portability;

import com.ossflow.identity.profile.application.UserProfileService;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IdentityExporter {

    private final UserProfileService userProfileService;

    public Map<String, Object> exportFor(Long ownerId) {
        try {
            var profile = userProfileService.getProfileByOwner(ownerId);
            return Map.of("userProfile", profile);
        } catch (NotFoundException e) {
            return Map.of("userProfile", Map.of());
        }
    }
}
