package com.ruleengine.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a condition to be evaluated against an event payload.
 * 
 * Conditions can be:
 * 1. Simple: A single field comparison (field, op, value)
 * 2. Composite AND: All conditions in "all" list must be true
 * 3. Composite OR: At least one condition in "any" list must be true
 * 
 * Supported operators: ==, !=, >, <, >=, <=, contains
 * 
 * Example simple condition:
 * { "field": "failCount", "op": ">=", "value": 3 }
 * 
 * Example composite AND:
 * {
 * "all": [
 * { "field": "failCount", "op": ">=", "value": 3 },
 * { "field": "ipCountry", "op": "!=", "value": "TR" }
 * ]
 * }
 * 
 * Example composite OR:
 * {
 * "any": [
 * { "field": "userRole", "op": "==", "value": "ADMIN" },
 * { "field": "userRole", "op": "==", "value": "MODERATOR" }
 * ]
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition {

    // For simple conditions
    private String field;
    private String op;
    private Object value;

    // For composite conditions
    private List<Condition> all; // AND
    private List<Condition> any; // OR

    public Condition() {
    }

    // Constructor for simple condition
    public Condition(String field, String op, Object value) {
        this.field = field;
        this.op = op;
        this.value = value;
    }

    // Static factory for AND conditions
    public static Condition and(List<Condition> conditions) {
        Condition c = new Condition();
        c.setAll(conditions);
        return c;
    }

    // Static factory for OR conditions
    public static Condition or(List<Condition> conditions) {
        Condition c = new Condition();
        c.setAny(conditions);
        return c;
    }

    /**
     * Checks if this is a simple (leaf) condition.
     */
    public boolean isSimple() {
        return field != null && op != null;
    }

    /**
     * Checks if this is an AND composite condition.
     */
    public boolean isAnd() {
        return all != null && !all.isEmpty();
    }

    /**
     * Checks if this is an OR composite condition.
     */
    public boolean isOr() {
        return any != null && !any.isEmpty();
    }

    // Getters and Setters
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<Condition> getAll() {
        return all;
    }

    public void setAll(List<Condition> all) {
        this.all = all;
    }

    public List<Condition> getAny() {
        return any;
    }

    public void setAny(List<Condition> any) {
        this.any = any;
    }

    @Override
    public String toString() {
        if (isSimple()) {
            return "Condition{" + field + " " + op + " " + value + "}";
        } else if (isAnd()) {
            return "Condition{AND: " + all + "}";
        } else if (isOr()) {
            return "Condition{OR: " + any + "}";
        }
        return "Condition{empty}";
    }
}
