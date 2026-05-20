package com.ossflow.coaching.competition;

import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import com.ossflow.journal.competitionlog.application.CompetitionLogService;
import com.ossflow.journal.competitionlog.infrastructure.web.CompetitionLogWebMapper;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.CompetitionLogResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('COACH')")
@RestController
@RequestMapping("/api/v1/coaching/athletes")
@Validated
@RequiredArgsConstructor
public class CoachCompetitionController {

    private final CompetitionLogService service;
    private final CompetitionLogWebMapper mapper;

    @GetMapping("/{athleteId}/competition-logs")
    public Page<CompetitionLogResponse> list(
            @PathVariable @Positive Long athleteId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @AuthenticationPrincipal AccountPrincipal principal) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.DESC, "eventDate"));
        return service.listForCoach(principal.id(), athleteId, pageable)
                .map(mapper::toResponse);
    }
}
