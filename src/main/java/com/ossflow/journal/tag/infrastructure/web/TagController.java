package com.ossflow.journal.tag.infrastructure.web;

import com.ossflow.journal.tag.application.TagService;
import com.ossflow.journal.tag.infrastructure.web.dto.CreateTagRequest;
import com.ossflow.journal.tag.infrastructure.web.dto.TagResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/journal/tags")
@Validated
@RequiredArgsConstructor
public class TagController {

    private final TagService service;
    private final TagWebMapper mapper;

    @GetMapping
    public List<TagResponse> list(
            @RequestParam(required = false, defaultValue = "") String prefix,
            @RequestParam(defaultValue = "20") @Min(1) int limit) {
        if (prefix.isBlank()) {
            return service.findAll().stream().map(mapper::toResponse).toList();
        }
        return service.findByNamePrefix(prefix, Math.min(limit, 100))
                .stream().map(mapper::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<TagResponse> create(@Valid @RequestBody CreateTagRequest req) {
        var tag = service.findOrCreate(req.name());
        return ResponseEntity
                .created(URI.create("/api/v1/journal/tags/" + tag.id()))
                .body(mapper.toResponse(tag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
