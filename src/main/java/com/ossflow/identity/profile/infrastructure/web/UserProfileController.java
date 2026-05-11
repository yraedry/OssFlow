package com.ossflow.identity.profile.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.identity.profile.application.UserProfileService;
import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.profile.domain.UserProfileFederation;
import com.ossflow.identity.profile.infrastructure.web.dto.CreateUserProfileRequest;
import com.ossflow.identity.profile.infrastructure.web.dto.UpdateUserProfileRequest;
import com.ossflow.identity.profile.infrastructure.web.dto.UserProfileFederationRequest;
import com.ossflow.identity.profile.infrastructure.web.dto.UserProfileResponse;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/identity/profile")
@Validated
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;
    private final UserProfileWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public UserProfileResponse getProfile() {
        return mapper.toResponse(service.getProfileByOwner(currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> createProfile(@Valid @RequestBody CreateUserProfileRequest req) {
        UserProfile toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        UserProfile created = service.createProfile(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/identity/profile"))
                .body(mapper.toResponse(created));
    }

    @PutMapping
    public UserProfileResponse updateProfile(@Valid @RequestBody UpdateUserProfileRequest req) {
        UserProfile patch = mapper.fromUpdate(req);
        return mapper.toResponse(service.updateProfile(currentOwner.id(), patch));
    }

    @PatchMapping
    public UserProfileResponse patchProfile(@RequestBody UpdateUserProfileRequest req) {
        UserProfile patch = mapper.fromUpdate(req);
        return mapper.toResponse(service.updateProfile(currentOwner.id(), patch));
    }

    @PutMapping("/federations")
    public UserProfileResponse replaceFederations(@RequestBody List<UserProfileFederationRequest> reqs) {
        List<UserProfileFederation> federations = reqs.stream()
                .map(mapper::fromFederationRequest)
                .toList();
        return mapper.toResponse(service.replaceFederations(currentOwner.id(), federations));
    }
}
