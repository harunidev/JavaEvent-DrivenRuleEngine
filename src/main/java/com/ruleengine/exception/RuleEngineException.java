package com.ruleengine.exception;

/**
 * Base exception class for the Rule Engine.
 */
public class RuleEngineException extends RuntimeException {

    public RuleEngineException(String message) {
        super(message);
    }

    public RuleEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
