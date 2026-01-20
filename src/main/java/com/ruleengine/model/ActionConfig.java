package com.ruleengine.model;

/**
 * Represents the action configuration for a rule.
 * 
 * Contains the action type and optional parameters for action execution.
 */
public class ActionConfig {

    private ActionType action;
    private String message; // Optional message for notifications

    public ActionConfig() {
    }

    public ActionConfig(ActionType action) {
        this.action = action;
    }

    public ActionConfig(ActionType action, String message) {
        this.action = action;
        this.message = message;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ActionConfig{action=" + action + ", message='" + message + "'}";
    }
}
