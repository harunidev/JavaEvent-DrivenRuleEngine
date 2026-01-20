package com.ruleengine.api;

import com.ruleengine.exception.InvalidConditionException;
import com.ruleengine.exception.RuleEngineException;
import com.ruleengine.model.EvaluationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Rule Engine API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidConditionException.class)
    public ResponseEntity<Object> handleInvalidCondition(InvalidConditionException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RuleEngineException.class)
    public ResponseEntity<Object> handleRuleEngineException(RuleEngineException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        // Log the full stack trace for debugging
        // Logger should be injected here
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal error occurred. Please contact support.");
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}
