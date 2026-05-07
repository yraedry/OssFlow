package com.ossflow.journal.physicalsession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.journal.physicalsession.application.PhysicalSessionService;
import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;
import com.ossflow.journal.physicalsession.infrastructure.web.PhysicalSessionController;
import com.ossflow.journal.physicalsession.infrastructure.web.PhysicalSessionWebMapper;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.CreatePhysicalSessionRequest;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.shared.web.CurrentOwner;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PhysicalSessionControllerTest {

    private final PhysicalSessionService service = mock(PhysicalSessionService.class);
    private final PhysicalSessionWebMapper mapper = new PhysicalSessionWebMapper();
    private final CurrentOwner currentOwner = mock(CurrentOwner.class);
    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new PhysicalSessionController(service, mapper, currentOwner))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    private PhysicalSession sample() {
        return PhysicalSession.builder()
                .id(1L)
                .ownerId(42L)
                .sessionDate(LocalDate.of(2026, 5, 7))
                .sessionType(PhysicalSessionType.STRENGTH)
                .title("Fuerza — Empuje")
                .durationMinutes(60)
                .notes(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .version(0L)
                .build();
    }

    @Test
    void list_returns_200() throws Exception {
        when(currentOwner.id()).thenReturn(42L);
        when(service.list(eq(42L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sample()), PageRequest.of(0, 20), 1));

        mvc.perform(get("/api/v1/journal/physical-sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Fuerza — Empuje"));
    }

    @Test
    void create_returns_201() throws Exception {
        when(currentOwner.id()).thenReturn(42L);
        when(service.create(any())).thenReturn(sample());

        CreatePhysicalSessionRequest req = new CreatePhysicalSessionRequest(
                LocalDate.of(2026, 5, 7),
                PhysicalSessionType.STRENGTH,
                "Fuerza — Empuje",
                60,
                null
        );

        mvc.perform(post("/api/v1/journal/physical-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionType").value("STRENGTH"));
    }

    @Test
    void delete_returns_204() throws Exception {
        when(currentOwner.id()).thenReturn(42L);

        mvc.perform(delete("/api/v1/journal/physical-sessions/1"))
                .andExpect(status().isNoContent());
    }
}
