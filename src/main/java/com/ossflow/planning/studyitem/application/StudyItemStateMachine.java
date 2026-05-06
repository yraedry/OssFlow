package com.ossflow.planning.studyitem.application;

import com.ossflow.planning.studyitem.domain.StudyItemStatus;
import com.ossflow.shared.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.ossflow.planning.studyitem.domain.StudyItemStatus.*;

@Component
public class StudyItemStateMachine {

    private static final Map<StudyItemStatus, Set<StudyItemStatus>> ALLOWED = Map.of(
            TODO,    Set.of(DOING, DONE, SKIPPED),
            DOING,   Set.of(TODO, DONE, SKIPPED),
            DONE,    Set.of(TODO),
            SKIPPED, Set.of(TODO)
    );

    public void assertTransition(StudyItemStatus from, StudyItemStatus to) {
        var allowed = ALLOWED.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new InvalidStateTransitionException("INVALID_STATE_TRANSITION",
                    "No se puede pasar de %s a %s".formatted(from, to),
                    Map.of("from", from, "to", to, "allowed", allowed));
        }
    }
}
