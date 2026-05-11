package com.ossflow.journal.note.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.journal.note.application.NoteService;
import com.ossflow.journal.note.domain.Note;
import com.ossflow.journal.note.infrastructure.web.dto.CreateNoteRequest;
import com.ossflow.journal.note.infrastructure.web.dto.NoteResponse;
import com.ossflow.journal.note.infrastructure.web.dto.PatchNoteRequest;
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

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/journal/notes")
@Validated
@RequiredArgsConstructor
public class NoteController {

    private final NoteService service;
    private final NoteWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<NoteResponse> list(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.list(currentOwner.id(), targetType, targetId, tag, q, pageable)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public NoteResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody CreateNoteRequest req) {
        Note toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        Note created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/journal/notes/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PatchMapping("/{id}")
    public NoteResponse patch(@PathVariable @Positive Long id,
                              @Valid @RequestBody PatchNoteRequest req) {
        Note existing = service.findById(id, currentOwner.id());
        Note patched = mapper.applyPatch(req, existing);
        return mapper.toResponse(service.patch(id, currentOwner.id(), patched));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public NoteResponse restore(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.restore(id, currentOwner.id()));
    }
}
