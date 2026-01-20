package com.ruleengine.exception;

/**
 * Exception thrown when a condition is invalid or cannot be evaluated.
 */
public class InvalidConditionException extends RuleEngineException {

    public InvalidConditionException(String message) {
        super(message);
    }

    public InvalidConditionException(String message, Throwable cause) {
        super(message, cause);
    }
}
