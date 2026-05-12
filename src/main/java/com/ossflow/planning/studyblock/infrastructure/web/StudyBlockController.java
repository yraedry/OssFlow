package com.ossflow.planning.studyblock.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.ossflow.planning.studyblock.application.StudyBlockService;
import com.ossflow.planning.studyblock.domain.StudyBlock;
import com.ossflow.planning.studyblock.infrastructure.web.dto.CreateStudyBlockRequest;
import com.ossflow.planning.studyblock.infrastructure.web.dto.StudyBlockResponse;
import com.ossflow.planning.studyblock.infrastructure.web.dto.UpdateStudyBlockRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/planning/study-plans/{pid}/blocks")
@Validated
@RequiredArgsConstructor
public class StudyBlockController {

    private final StudyBlockService service;
    private final StudyBlockWebMapper mapper;

    @GetMapping
    public List<StudyBlockResponse> list(@PathVariable @Positive Long pid) {
        return service.listByPlan(pid).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{bid}")
    public StudyBlockResponse get(@PathVariable @Positive Long pid,
                                  @PathVariable @Positive Long bid) {
        StudyBlock block = service.findById(bid);
        return mapper.toResponse(block);
    }

    @PostMapping
    public ResponseEntity<StudyBlockResponse> create(@PathVariable @Positive Long pid,
                                                     @Valid @RequestBody CreateStudyBlockRequest req) {
        StudyBlock toCreate = mapper.fromCreate(req).toBuilder().studyPlanId(pid).build();
        StudyBlock created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/planning/study-plans/" + pid + "/blocks/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{bid}")
    public StudyBlockResponse replace(@PathVariable @Positive Long pid,
                                      @PathVariable @Positive Long bid,
                                      @Valid @RequestBody UpdateStudyBlockRequest req) {
        StudyBlock updated = mapper.fromUpdate(req).toBuilder().studyPlanId(pid).build();
        return mapper.toResponse(service.update(bid, updated));
    }

    @DeleteMapping("/{bid}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long pid,
                                       @PathVariable @Positive Long bid) {
        service.delete(bid);
        return ResponseEntity.noContent().build();
    }
}
