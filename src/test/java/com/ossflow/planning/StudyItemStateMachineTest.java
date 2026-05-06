package com.ossflow.planning;

import com.ossflow.planning.studyitem.application.StudyItemStateMachine;
import com.ossflow.planning.studyitem.domain.StudyItemStatus;
import com.ossflow.shared.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.ossflow.planning.studyitem.domain.StudyItemStatus.*;

class StudyItemStateMachineTest {

    private final StudyItemStateMachine machine = new StudyItemStateMachine();

    @ParameterizedTest
    @CsvSource({
            "TODO, DOING",
            "TODO, DONE",
            "TODO, SKIPPED",
            "DOING, TODO",
            "DOING, DONE",
            "DOING, SKIPPED",
            "DONE, TODO",
            "SKIPPED, TODO"
    })
    void should_allow_valid_transitions(String from, String to) {
        assertThatCode(() -> machine.assertTransition(
                StudyItemStatus.valueOf(from), StudyItemStatus.valueOf(to)))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({
            "DONE, DOING",
            "DONE, SKIPPED",
            "SKIPPED, DOING",
            "SKIPPED, DONE"
    })
    void should_reject_invalid_transitions(String from, String to) {
        assertThatThrownBy(() -> machine.assertTransition(
                StudyItemStatus.valueOf(from), StudyItemStatus.valueOf(to)))
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasFieldOrPropertyWithValue("errorCode", "INVALID_STATE_TRANSITION");
    }

    @Test
    void should_allow_todo_to_todo_is_invalid() {
        assertThatThrownBy(() -> machine.assertTransition(TODO, TODO))
                .isInstanceOf(InvalidStateTransitionException.class);
    }
}
