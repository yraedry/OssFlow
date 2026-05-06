package com.ossflow.journal.note.application;

import com.ossflow.journal.note.application.port.NoteRepositoryPort;
import com.ossflow.journal.note.domain.Note;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock NoteRepositoryPort repository;
    @InjectMocks NoteService service;

    private Note buildNote(Long id) {
        return Note.builder()
                .id(id)
                .ownerId(1L)
                .title("Nota de prueba")
                .bodyMarkdown("Contenido de la nota")
                .tags(List.of("bjj", "guard"))
                .build();
    }

    @Test
    void should_return_saved_note_when_create_called() {
        var input = buildNote(null);
        var saved = buildNote(42L);
        given(repository.save(input)).willReturn(saved);

        var result = service.create(input);

        assertThat(result.id()).isEqualTo(42L);
        assertThat(result.title()).isEqualTo("Nota de prueba");
    }

    @Test
    void should_return_note_when_findById_exists() {
        var note = buildNote(10L);
        given(repository.findById(10L, 1L)).willReturn(Optional.of(note));

        var result = service.findById(10L, 1L);

        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void should_throw_NotFoundException_when_findById_not_found() {
        given(repository.findById(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "NOTE_NOT_FOUND");
    }

    @Test
    void should_return_page_when_list_called() {
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(buildNote(1L)), pageable, 1);
        given(repository.findAll(eq(1L), any(), any(), any(), any(), eq(pageable))).willReturn(page);

        var result = service.list(1L, null, null, null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void should_soft_delete_note_when_softDelete_called() {
        service.softDelete(5L, 1L);

        verify(repository).softDelete(5L, 1L);
    }

    @Test
    void should_return_restored_note_when_restore_called() {
        var restored = buildNote(7L);
        given(repository.restore(7L, 1L)).willReturn(restored);

        var result = service.restore(7L, 1L);

        assertThat(result.id()).isEqualTo(7L);
    }
}
