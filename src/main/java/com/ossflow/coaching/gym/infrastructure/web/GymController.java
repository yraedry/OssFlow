package com.ossflow.coaching.gym.infrastructure.web;

import com.ossflow.coaching.gym.application.GymService;
import com.ossflow.coaching.gym.infrastructure.web.dto.CreateGymRequest;
import com.ossflow.coaching.gym.infrastructure.web.dto.GymResponse;
import com.ossflow.coaching.gym.infrastructure.web.dto.UpdateGymRequest;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching/gyms")
@RequiredArgsConstructor
public class GymController {
    private final GymService service;

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public GymResponse create(@AuthenticationPrincipal AccountPrincipal principal,
                               @RequestBody @Valid CreateGymRequest req) {
        return GymResponse.from(service.create(principal.id(), req.name(), req.address()));
    }

    @GetMapping
    @PreAuthorize("hasRole('COACH')")
    public List<GymResponse> list(@AuthenticationPrincipal AccountPrincipal principal) {
        return service.list(principal.id()).stream().map(GymResponse::from).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public GymResponse update(@AuthenticationPrincipal AccountPrincipal principal,
                               @PathVariable Long id,
                               @RequestBody @Valid UpdateGymRequest req) {
        return GymResponse.from(service.update(id, principal.id(), req.name(), req.address()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AccountPrincipal principal, @PathVariable Long id) {
        service.delete(id, principal.id());
    }
}
