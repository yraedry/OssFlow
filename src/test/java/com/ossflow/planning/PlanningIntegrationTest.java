package com.ossflow.planning;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class PlanningIntegrationTest {

    @Autowired WebApplicationContext wac;
    MockMvc mvc;
    final ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void should_create_study_plan_with_blocks_and_items() throws Exception {
        // Create plan
        String planBody = """
                {"title": "Plan Guardia", "startDate": "2024-01-01", "endDate": "2024-03-31", "status": "DRAFT"}
                """;
        String planResp = mvc.perform(post("/api/v1/planning/study-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(planBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long planId = json.readTree(planResp).get("id").asLong();

        // Create block
        String blockBody = """
                {"title": "Bloque 1", "startDate": "2024-01-01", "endDate": "2024-01-31", "blockOrder": 1}
                """;
        String blockResp = mvc.perform(post("/api/v1/planning/study-plans/" + planId + "/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long blockId = json.readTree(blockResp).get("id").asLong();

        // Create item
        String itemBody = """
                {"description": "Practicar armbar", "status": "TODO"}
                """;
        String itemResp = mvc.perform(post("/api/v1/planning/study-plans/" + planId + "/blocks/" + blockId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andReturn().getResponse().getContentAsString();
        Long itemId = json.readTree(itemResp).get("id").asLong();

        // Transition TODO → DOING
        mvc.perform(post("/api/v1/planning/study-plans/" + planId + "/blocks/" + blockId + "/items/" + itemId + "/transition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetStatus\": \"DOING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DOING"));

        // Transition DOING → DONE
        mvc.perform(post("/api/v1/planning/study-plans/" + planId + "/blocks/" + blockId + "/items/" + itemId + "/transition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetStatus\": \"DONE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.completedAt").isNotEmpty());
    }

    @Test
    void should_reject_invalid_transition_with_409() throws Exception {
        // Create plan/block/item
        String planBody = """
                {"title": "Plan Test Trans", "startDate": "2024-01-01", "endDate": "2024-03-31", "status": "DRAFT"}
                """;
        String planResp = mvc.perform(post("/api/v1/planning/study-plans")
                .contentType(MediaType.APPLICATION_JSON).content(planBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long planId = json.readTree(planResp).get("id").asLong();

        String blockResp = mvc.perform(post("/api/v1/planning/study-plans/" + planId + "/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"B1\", \"startDate\": \"2024-01-01\", \"endDate\": \"2024-01-31\", \"blockOrder\": 1}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long blockId = json.readTree(blockResp).get("id").asLong();

        String itemResp = mvc.perform(post("/api/v1/planning/study-plans/" + planId + "/blocks/" + blockId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Item test\", \"status\": \"TODO\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long itemId = json.readTree(itemResp).get("id").asLong();

        // Transition TODO → DONE (allowed), then DONE → DOING (NOT allowed → 409)
        mvc.perform(post("/api/v1/planning/study-plans/" + planId + "/blocks/" + blockId + "/items/" + itemId + "/transition")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"targetStatus\": \"DONE\"}"))
                .andExpect(status().isOk());

        mvc.perform(post("/api/v1/planning/study-plans/" + planId + "/blocks/" + blockId + "/items/" + itemId + "/transition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetStatus\": \"DOING\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_STATE_TRANSITION"));
    }
}
