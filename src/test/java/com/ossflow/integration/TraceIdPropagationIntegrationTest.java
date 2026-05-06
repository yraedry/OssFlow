package com.ossflow.integration;

import com.ossflow.shared.web.RequestTracingFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class TraceIdPropagationIntegrationTest {

    @Autowired WebApplicationContext wac;
    @Autowired RequestTracingFilter requestTracingFilter;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(requestTracingFilter)
                .build();
    }

    @Test
    void should_echo_trace_id_in_response_header() throws Exception {
        var traceId = "trace-test-123";
        mvc.perform(get("/api/v1/catalog/positions").header("X-Trace-Id", traceId))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Trace-Id", traceId));
    }

    @Test
    void should_generate_trace_id_when_missing() throws Exception {
        var result = mvc.perform(get("/api/v1/catalog/positions"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Trace-Id"))
                .andReturn();
        var trace = result.getResponse().getHeader("X-Trace-Id");
        assertThat(trace).isNotBlank();
    }

    @Test
    void should_include_trace_id_in_error_response_body() throws Exception {
        mvc.perform(get("/api/v1/catalog/positions/9999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.traceId").exists());
    }
}
