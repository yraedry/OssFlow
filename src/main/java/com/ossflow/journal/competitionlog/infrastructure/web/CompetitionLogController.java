package com.ossflow.journal.competitionlog.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.journal.competitionlog.application.CompetitionLogService;
import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import com.ossflow.journal.competitionlog.domain.CompetitionMatch;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.AddCompetitionMatchRequest;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.CompetitionLogResponse;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.CompetitionMatchResponse;
import com.ossflow.journal.competitionlog.infrastructure.web.dto.CreateCompetitionLogRequest;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/journal/competition-logs")
@Validated
@RequiredArgsConstructor
public class CompetitionLogController {

    private final CompetitionLogService service;
    private final CompetitionLogWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<CompetitionLogResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "eventDate"));
        return service.list(currentOwner.id(), pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public CompetitionLogResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<CompetitionLogResponse> create(@Valid @RequestBody CreateCompetitionLogRequest req) {
        CompetitionLog toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        CompetitionLog created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/journal/competition-logs/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public CompetitionLogResponse replace(@PathVariable @Positive Long id,
                                          @Valid @RequestBody CreateCompetitionLogRequest req) {
        CompetitionLog replacement = mapper.fromCreate(req);
        return mapper.toResponse(service.replace(id, currentOwner.id(), replacement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public CompetitionLogResponse restore(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.restore(id, currentOwner.id()));
    }

    @GetMapping("/{id}/matches")
    public List<CompetitionMatchResponse> getMatches(@PathVariable @Positive Long id) {
        return service.getMatches(id, currentOwner.id())
                .stream().map(mapper::toMatchResponse).toList();
    }

    @PostMapping("/{id}/matches")
    public ResponseEntity<CompetitionLogResponse> addMatch(
            @PathVariable @Positive Long id,
            @Valid @RequestBody AddCompetitionMatchRequest req) {
        CompetitionMatch match = mapper.fromMatchRequest(req);
        CompetitionLog updated = service.addMatch(id, currentOwner.id(), match);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}/matches/{mid}")
    public ResponseEntity<Void> removeMatch(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long mid) {
        service.removeMatch(id, currentOwner.id(), mid);
        return ResponseEntity.noContent().build();
    }
}
