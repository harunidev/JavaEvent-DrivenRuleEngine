package com.ruleengine.model.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruleengine.model.ActionConfig;
import com.ruleengine.model.Condition;
import com.ruleengine.model.Rule;
import jakarta.persistence.*;

@Entity
@Table(name = "rules")
public class RuleEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String name;

    private int priority;

    @Column(columnDefinition = "TEXT")
    private String conditionJson;

    @Column(columnDefinition = "TEXT")
    private String actionJson;

    private boolean enabled;

    private String description;

    // Helper for JSON conversion
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public RuleEntity() {
    }

    public static RuleEntity fromDomain(Rule rule) {
        RuleEntity entity = new RuleEntity();
        entity.setName(rule.getName());
        entity.setPriority(rule.getPriority());
        entity.setEnabled(rule.isEnabled());
        entity.setDescription(rule.getDescription());

        try {
            entity.setConditionJson(objectMapper.writeValueAsString(rule.getWhen()));
            entity.setActionJson(objectMapper.writeValueAsString(rule.getThen()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting rule to entity", e);
        }

        return entity;
    }

    public Rule toDomain() {
        Rule rule = new Rule();
        rule.setName(this.name);
        rule.setPriority(this.priority);
        rule.setEnabled(this.enabled);
        rule.setDescription(this.description);

        try {
            if (this.conditionJson != null && !this.conditionJson.isEmpty()) {
                rule.setWhen(objectMapper.readValue(this.conditionJson, Condition.class));
            }
            if (this.actionJson != null && !this.actionJson.isEmpty()) {
                rule.setThen(objectMapper.readValue(this.actionJson, ActionConfig.class));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting entity to rule: " + e.getMessage(), e);
        }

        return rule;
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

    public String getConditionJson() {
        return conditionJson;
    }

    public void setConditionJson(String conditionJson) {
        this.conditionJson = conditionJson;
    }

    public String getActionJson() {
        return actionJson;
    }

    public void setActionJson(String actionJson) {
        this.actionJson = actionJson;
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
}
