package com.ruleengine.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Represents an event that triggers rule evaluation.
 * 
 * An event consists of a type (e.g., "LOGIN", "PURCHASE") and a payload
 * containing the data to be evaluated against rules.
 * 
 * Example:
 * {
 * "type": "LOGIN",
 * "payload": {
 * "userRole": "ADMIN",
 * "ipCountry": "US",
 * "failCount": 3
 * }
 * }
 */
public class Event {

    @NotBlank(message = "Event type is required")
    private String type;

    @NotNull(message = "Payload is required")
    private Map<String, Object> payload;

    public Event() {
    }

    public Event(String type, Map<String, Object> payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Event{type='" + type + "', payload=" + payload + "}";
    }
}
