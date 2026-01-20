package com.ruleengine.model;

/**
 * Captures detailed information about a single rule's execution.
 * Used for debugging why a rule matched or failed.
 */
public class RuleTrace {

    private String ruleName;
    private boolean matched;
    private String failureReason; // e.g., "Condition failCount >= 3 failed: actual=1"

    public RuleTrace() {
    }

    public RuleTrace(String ruleName, boolean matched) {
        this.ruleName = ruleName;
        this.matched = matched;
    }

    public RuleTrace(String ruleName, boolean matched, String failureReason) {
        this.ruleName = ruleName;
        this.matched = matched;
        this.failureReason = failureReason;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @Override
    public String toString() {
        if (matched) {
            return "RuleTrace{" + ruleName + ": MATCH}";
        }
        return "RuleTrace{" + ruleName + ": NO MATCH (" + failureReason + ")}";
    }
}
