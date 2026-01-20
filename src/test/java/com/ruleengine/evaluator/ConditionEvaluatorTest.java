package com.ruleengine.evaluator;

import com.ruleengine.model.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConditionEvaluatorTest {

    private ConditionEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new ConditionEvaluator();
    }

    @Test
    void testSimpleComparisonMatches() {
        Condition condition = new Condition("age", ">=", 18);
        Map<String, Object> payload = new HashMap<>();
        payload.put("age", 20);

        EvaluationDetail result = evaluator.evaluate(condition, payload);
        assertTrue(result.isMatched());
    }

    @Test
    void testSimpleComparisonFails() {
        Condition condition = new Condition("age", ">=", 18);
        Map<String, Object> payload = new HashMap<>();
        payload.put("age", 15);

        EvaluationDetail result = evaluator.evaluate(condition, payload);
        assertFalse(result.isMatched());
        assertTrue(result.getReason().contains("failed"));
    }

    @Test
    void testNestedFieldAccess() {
        Condition condition = new Condition("user.address.city", "==", "Istanbul");
        Map<String, Object> address = new HashMap<>();
        address.put("city", "Istanbul");
        Map<String, Object> user = new HashMap<>();
        user.put("address", address);
        Map<String, Object> payload = new HashMap<>();
        payload.put("user", user);

        EvaluationDetail result = evaluator.evaluate(condition, payload);
        assertTrue(result.isMatched());
    }

    @Test
    void testAndConditionMatches() {
        List<Condition> subConditions = new ArrayList<>();
        subConditions.add(new Condition("age", ">", 18));
        subConditions.add(new Condition("role", "==", "USER"));

        Condition condition = Condition.and(subConditions);

        Map<String, Object> payload = new HashMap<>();
        payload.put("age", 25);
        payload.put("role", "USER");

        assertTrue(evaluator.evaluate(condition, payload).isMatched());
    }

    @Test
    void testAndConditionFails() {
        List<Condition> subConditions = new ArrayList<>();
        subConditions.add(new Condition("age", ">", 18));
        subConditions.add(new Condition("role", "==", "ADMIN"));

        Condition condition = Condition.and(subConditions);

        Map<String, Object> payload = new HashMap<>();
        payload.put("age", 25);
        payload.put("role", "USER");

        assertFalse(evaluator.evaluate(condition, payload).isMatched());
    }

    @Test
    void testOrConditionMatches() {
        List<Condition> subConditions = new ArrayList<>();
        subConditions.add(new Condition("role", "==", "ADMIN"));
        subConditions.add(new Condition("role", "==", "MODERATOR"));

        Condition condition = Condition.or(subConditions);

        Map<String, Object> payload = new HashMap<>();
        payload.put("role", "MODERATOR");

        assertTrue(evaluator.evaluate(condition, payload).isMatched());
    }

    @Test
    void testRegexMatches() {
        Condition condition = new Condition("email", "matches", "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "test@example.com");

        assertTrue(evaluator.evaluate(condition, payload).isMatched());
    }

    @Test
    void testDateComparison() {
        Condition condition = new Condition("expiryDate", "dateAfter", "2023-01-01T00:00:00");
        Map<String, Object> payload = new HashMap<>();
        payload.put("expiryDate", "2024-01-01T12:00:00");

        assertTrue(evaluator.evaluate(condition, payload).isMatched());
    }

    @Test
    void testListContains() {
        Condition condition = new Condition("tags", "contains", "VIP");
        Map<String, Object> payload = new HashMap<>();
        payload.put("tags", List.of("NEW_USER", "VIP", "MOBILE"));

        assertTrue(evaluator.evaluate(condition, payload).isMatched());
    }
}
