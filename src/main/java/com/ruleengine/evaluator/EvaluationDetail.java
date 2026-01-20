package com.ruleengine.evaluator;

public class EvaluationDetail {
    private boolean matched;
    private String reason;

    public EvaluationDetail(boolean matched) {
        this.matched = matched;
    }

    public EvaluationDetail(boolean matched, String reason) {
        this.matched = matched;
        this.reason = reason;
    }

    public boolean isMatched() {
        return matched;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return matched ? "MATCH" : "FAIL: " + reason;
    }
}
