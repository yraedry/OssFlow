package com.ossflow.coaching.classplan.infrastructure.web;

import com.ossflow.coaching.classplan.application.ClassPlanService;
import com.ossflow.coaching.classplan.infrastructure.web.dto.ClassPlanResponse;
import com.ossflow.coaching.classplan.infrastructure.web.dto.CreateClassPlanRequest;
import com.ossflow.coaching.classplan.infrastructure.web.dto.UpdateClassPlanRequest;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coaching/class-plans")
@RequiredArgsConstructor
public class ClassPlanController {

    private final ClassPlanService service;

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.CREATED)
    public ClassPlanResponse create(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestBody @Valid CreateClassPlanRequest req) {
        return ClassPlanResponse.from(service.create(
                principal.id(), req.gymId(), req.title(), req.description(),
                req.scheduledDate(), req.durationMinutes(), req.modality()));
    }

    @GetMapping
    @PreAuthorize("hasRole('COACH')")
    public List<ClassPlanResponse> list(
            @AuthenticationPrincipal AccountPrincipal principal,
            @RequestParam Long gymId) {
        return service.list(principal.id(), gymId)
                .stream().map(ClassPlanResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public ClassPlanResponse get(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        return ClassPlanResponse.from(service.get(id, principal.id()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public ClassPlanResponse update(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id,
            @RequestBody @Valid UpdateClassPlanRequest req) {
        return ClassPlanResponse.from(service.update(
                id, principal.id(), req.title(), req.description(),
                req.scheduledDate(), req.durationMinutes(), req.modality(), req.status()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long id) {
        service.delete(id, principal.id());
    }
}
