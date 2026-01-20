package com.ruleengine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of evaluating rules against an event.
 * 
 * Contains information about:
 * - matchedRules: Names of rules that matched the event
 * - actions: Actions that were triggered
 * - executionTimeMs: Time taken for evaluation
 * - eventType: The type of event that was evaluated
 */
public class EvaluationResult {

    private String eventType;
    private List<String> matchedRules;
    private List<ActionType> actions;
    private long executionTimeMs;
    private int totalRulesEvaluated;
    private boolean success;
    private String errorMessage;
    private java.util.List<RuleTrace> trace;

    public EvaluationResult() {
        this.matchedRules = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.trace = new ArrayList<>();
        this.success = true;
    }

    public EvaluationResult(String eventType) {
        this();
        this.eventType = eventType;
    }

    /**
     * Adds a matched rule and its action to the result.
     */
    public void addMatch(Rule rule) {
        this.matchedRules.add(rule.getName());
        if (rule.getThen() != null && rule.getThen().getAction() != null) {
            this.actions.add(rule.getThen().getAction());
        }
    }

    public void addTrace(RuleTrace ruleTrace) {
        this.trace.add(ruleTrace);
    }

    /**
     * Creates an error result.
     */
    public static EvaluationResult error(String eventType, String errorMessage) {
        EvaluationResult result = new EvaluationResult(eventType);
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<String> getMatchedRules() {
        return matchedRules;
    }

    public void setMatchedRules(List<String> matchedRules) {
        this.matchedRules = matchedRules;
    }

    public List<ActionType> getActions() {
        return actions;
    }

    public void setActions(List<ActionType> actions) {
        this.actions = actions;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public int getTotalRulesEvaluated() {
        return totalRulesEvaluated;
    }

    public void setTotalRulesEvaluated(int totalRulesEvaluated) {
        this.totalRulesEvaluated = totalRulesEvaluated;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<RuleTrace> getTrace() {
        return trace;
    }

    public void setTrace(List<RuleTrace> trace) {
        this.trace = trace;
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
                "eventType='" + eventType + '\'' +
                ", matchedRules=" + matchedRules +
                ", actions=" + actions +
                ", executionTimeMs=" + executionTimeMs +
                ", totalRulesEvaluated=" + totalRulesEvaluated +
                ", success=" + success +
                '}';
    }
}
