package com.ossflow.journal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JournalIntegrationTest {

    @Autowired WebApplicationContext wac;
    MockMvc mvc;
    final ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        TestSecurityContext.setOwner(1L);
    }

    @AfterEach
    void tearDownAuth() { TestSecurityContext.clear(); }

    @Test
    void should_create_note_with_two_new_tags() throws Exception {
        String body = """
                {
                  "title": "Nota de prueba",
                  "bodyMarkdown": "# Contenido",
                  "tags": ["bjj", "guardia"]
                }
                """;
        mvc.perform(post("/api/v1/journal/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tags.length()").value(2))
                .andExpect(jsonPath("$.title").value("Nota de prueba"));
    }

    @Test
    void should_reuse_existing_tag_when_creating_second_note() throws Exception {
        String body1 = """
                {"title": "Nota 1", "bodyMarkdown": "# One", "tags": ["bjj"]}
                """;
        mvc.perform(post("/api/v1/journal/notes")
                .contentType(MediaType.APPLICATION_JSON).content(body1))
                .andExpect(status().isCreated());

        String body2 = """
                {"title": "Nota 2", "bodyMarkdown": "# Two", "tags": ["bjj", "kimura"]}
                """;
        mvc.perform(post("/api/v1/journal/notes")
                        .contentType(MediaType.APPLICATION_JSON).content(body2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tags.length()").value(2));

        // tag 'bjj' was reused, only 2 tags total (bjj + kimura) in the system
        mvc.perform(get("/api/v1/journal/tags?prefix=bjj"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void should_create_training_session() throws Exception {
        String body = """
                {
                  "sessionDate": "2024-01-15",
                  "durationMinutes": 90,
                  "location": "Gym Central",
                  "intensity": "MODERATE",
                  "sessionType": "BJJ",
                  "notesMarkdown": "Good session"
                }
                """;
        mvc.perform(post("/api/v1/journal/training-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.durationMinutes").value(90))
                .andExpect(jsonPath("$.intensity").value("MODERATE"));
    }

    @Test
    void should_create_competition_log_with_matches() throws Exception {
        String body = """
                {
                  "eventName": "Campeonato Regional 2024",
                  "eventDate": "2024-03-10",
                  "weightCategory": "-76kg",
                  "totalMatches": 3,
                  "result": "GOLD"
                }
                """;
        String respBody = mvc.perform(post("/api/v1/journal/competition-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventName").value("Campeonato Regional 2024"))
                .andReturn().getResponse().getContentAsString();

        Long logId = json.readTree(respBody).get("id").asLong();

        // Add matches via sub-resource
        for (int i = 1; i <= 3; i++) {
            String matchBody = """
                    {"matchOrder": %d, "opponentName": "Oponente %d", "outcome": "WIN", "method": "POINTS"}
                    """.formatted(i, i);
            mvc.perform(post("/api/v1/journal/competition-logs/" + logId + "/matches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(matchBody))
                    .andExpect(status().isOk());
        }

        mvc.perform(get("/api/v1/journal/competition-logs/" + logId + "/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        // DELETE competition log → matches fall by CASCADE
        mvc.perform(delete("/api/v1/journal/competition-logs/" + logId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/journal/competition-logs/" + logId))
                .andExpect(status().isNotFound());
    }
}
