package com.ruleengine.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents a rule in the rule engine.
 * 
 * A rule consists of:
 * - name: Unique identifier for the rule
 * - priority: Execution order (lower = higher priority)
 * - when: Condition that must be satisfied
 * - then: Action to execute when condition is met
 * 
 * Example:
 * {
 * "name": "BlockSuspiciousLogin",
 * "priority": 10,
 * "when": {
 * "all": [
 * { "field": "failCount", "op": ">=", "value": 3 },
 * { "field": "ipCountry", "op": "!=", "value": "TR" }
 * ]
 * },
 * "then": {
 * "action": "BLOCK_USER"
 * }
 * }
 */
public class Rule implements Comparable<Rule> {

    @NotBlank(message = "Rule name is required")
    @Size(max = 50, message = "Rule name cannot exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Rule name can only contain alphanumeric characters, underscores, and hyphens")
    private String name;

    private int priority = 100; // Default priority (lower = runs first)

    @NotNull(message = "Condition (when) is required")
    private Condition when;

    @NotNull(message = "Action (then) is required")
    private ActionConfig then;

    private boolean enabled = true;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    public Rule() {
    }

    public Rule(String name, int priority, Condition when, ActionConfig then) {
        this.name = name;
        this.priority = priority;
        this.when = when;
        this.then = then;
    }

    @Override
    public int compareTo(Rule other) {
        return Integer.compare(this.priority, other.priority);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Condition getWhen() {
        return when;
    }

    public void setWhen(Condition when) {
        this.when = when;
    }

    public ActionConfig getThen() {
        return then;
    }

    public void setThen(ActionConfig then) {
        this.then = then;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Rule{name='" + name + "', priority=" + priority +
                ", enabled=" + enabled + ", when=" + when + ", then=" + then + "}";
    }
}
