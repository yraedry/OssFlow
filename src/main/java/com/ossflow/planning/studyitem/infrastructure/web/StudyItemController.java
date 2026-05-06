package com.ossflow.planning.studyitem.infrastructure.web;

import com.ossflow.planning.studyitem.application.StudyItemService;
import com.ossflow.planning.studyitem.domain.StudyItem;
import com.ossflow.planning.studyitem.infrastructure.web.dto.CreateStudyItemRequest;
import com.ossflow.planning.studyitem.infrastructure.web.dto.StudyItemResponse;
import com.ossflow.planning.studyitem.infrastructure.web.dto.TransitionRequest;
import com.ossflow.planning.studyitem.infrastructure.web.dto.UpdateStudyItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/planning/study-plans/{pid}/blocks/{bid}/items")
@Validated
@RequiredArgsConstructor
public class StudyItemController {

    private final StudyItemService service;
    private final StudyItemWebMapper mapper;

    @GetMapping
    public List<StudyItemResponse> list(@PathVariable @Positive Long pid,
                                        @PathVariable @Positive Long bid) {
        return service.listByBlock(bid).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{iid}")
    public StudyItemResponse get(@PathVariable @Positive Long pid,
                                 @PathVariable @Positive Long bid,
                                 @PathVariable @Positive Long iid) {
        return mapper.toResponse(service.findById(iid));
    }

    @PostMapping
    public ResponseEntity<StudyItemResponse> create(@PathVariable @Positive Long pid,
                                                    @PathVariable @Positive Long bid,
                                                    @Valid @RequestBody CreateStudyItemRequest req) {
        StudyItem toCreate = mapper.fromCreate(req).toBuilder().studyBlockId(bid).build();
        StudyItem created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/planning/study-plans/" + pid + "/blocks/" + bid + "/items/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{iid}")
    public StudyItemResponse replace(@PathVariable @Positive Long pid,
                                     @PathVariable @Positive Long bid,
                                     @PathVariable @Positive Long iid,
                                     @Valid @RequestBody UpdateStudyItemRequest req) {
        StudyItem updated = mapper.fromUpdate(req).toBuilder().studyBlockId(bid).build();
        return mapper.toResponse(service.update(iid, updated));
    }

    @DeleteMapping("/{iid}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long pid,
                                       @PathVariable @Positive Long bid,
                                       @PathVariable @Positive Long iid) {
        service.delete(iid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{iid}/transition")
    public StudyItemResponse transition(@PathVariable @Positive Long pid,
                                        @PathVariable @Positive Long bid,
                                        @PathVariable @Positive Long iid,
                                        @Valid @RequestBody TransitionRequest req) {
        return mapper.toResponse(service.transition(iid, req.targetStatus()));
    }
}
