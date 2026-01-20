package com.ruleengine.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruleengine.model.ActionConfig;
import com.ruleengine.model.ActionType;
import com.ruleengine.model.Condition;
import com.ruleengine.model.Event;
import com.ruleengine.model.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RuleEngineIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testEndToEndRuleExecution() throws Exception {
        // 1. Create a Rule
        Rule rule = new Rule();
        rule.setName("IntegrationTestRule");
        rule.setPriority(1);

        Condition condition = new Condition("score", ">=", 50);
        rule.setWhen(condition);

        ActionConfig action = new ActionConfig(ActionType.APPROVE, "Auto approved by integration test");
        rule.setThen(action);

        mockMvc.perform(post("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rule)))
                .andExpect(status().isCreated());

        // 2. Send matching event
        Map<String, Object> payload = new HashMap<>();
        payload.put("score", 60);
        Event event = new Event("TEST_EVENT", payload);

        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.matchedRules", hasItem("IntegrationTestRule")))
                .andExpect(jsonPath("$.actions", hasItem("APPROVE")))
                .andExpect(jsonPath("$.trace[?(@.ruleName == 'IntegrationTestRule')].matched").value(true));
    }

    @Test
    void testRuleMismatchTrace() throws Exception {
        // 1. Create a Rule
        Rule rule = new Rule();
        rule.setName("MismatchTestRule");
        rule.setPriority(1);

        Condition condition = new Condition("role", "==", "ADMIN");
        rule.setWhen(condition);

        rule.setThen(new ActionConfig(ActionType.NOTIFY));

        mockMvc.perform(post("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rule)))
                .andExpect(status().isCreated());

        // 2. Send non-matching event
        Map<String, Object> payload = new HashMap<>();
        payload.put("role", "USER");
        Event event = new Event("TEST_MISMATCH", payload);

        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchedRules", hasSize(0)))
                .andExpect(jsonPath("$.trace[*].matched", hasItem(false)))
                .andExpect(jsonPath("$.trace[*].failureReason", notNullValue()));
    }
}
