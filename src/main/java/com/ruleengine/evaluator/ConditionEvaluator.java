package com.ruleengine.evaluator;

import com.ruleengine.exception.InvalidConditionException;
import com.ruleengine.model.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Evaluates conditions against event payloads.
 * 
 * Supports:
 * - Simple conditions: field op value comparisons
 * - Composite AND/OR nested conditions
 * - Operators: ==, !=, >, <, >=, <=, contains, matches, dateBefore, dateAfter
 */
@Component
public class ConditionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(ConditionEvaluator.class);

    /**
     * Evaluates a condition against the given payload.
     */
    public EvaluationDetail evaluate(Condition condition, Map<String, Object> payload) {
        if (condition == null) {
            throw new InvalidConditionException("Condition cannot be null");
        }

        if (condition.isSimple()) {
            return evaluateSimple(condition, payload);
        } else if (condition.isAnd()) {
            return evaluateAnd(condition, payload);
        } else if (condition.isOr()) {
            return evaluateOr(condition, payload);
        } else {
            throw new InvalidConditionException("Invalid condition: must be simple, AND, or OR");
        }
    }

    private EvaluationDetail evaluateSimple(Condition condition, Map<String, Object> payload) {
        String field = condition.getField();
        String op = condition.getOp();
        Object expectedValue = condition.getValue();

        if (field == null || op == null) {
            throw new InvalidConditionException("Simple condition requires 'field' and 'op'");
        }

        Object actualValue = getFieldValue(field, payload);

        boolean result;
        try {
            result = compare(actualValue, op, expectedValue);
        } catch (Exception e) {
            return new EvaluationDetail(false, "Error in comparison for field '" + field + "': " + e.getMessage());
        }

        if (result) {
            return new EvaluationDetail(true);
        } else {
            return new EvaluationDetail(false,
                    String.format("Condition '%s %s %s' failed. Actual: '%s'",
                            field, op, expectedValue, actualValue));
        }
    }

    private EvaluationDetail evaluateAnd(Condition condition, Map<String, Object> payload) {
        for (Condition subCondition : condition.getAll()) {
            EvaluationDetail result = evaluate(subCondition, payload);
            if (!result.isMatched()) {
                return result; // Short-circuit: return failure detail
            }
        }
        return new EvaluationDetail(true);
    }

    private EvaluationDetail evaluateOr(Condition condition, Map<String, Object> payload) {
        StringBuilder failureReasons = new StringBuilder();
        for (Condition subCondition : condition.getAny()) {
            EvaluationDetail result = evaluate(subCondition, payload);
            if (result.isMatched()) {
                return new EvaluationDetail(true); // Short-circuit: return match
            }
            failureReasons.append("[").append(result.getReason()).append("] ");
        }
        return new EvaluationDetail(false, "No condition in OR group matched. Details: " + failureReasons);
    }

    /**
     * Gets a field value from the payload.
     * Supports nested field access using dot notation (e.g., "user.address.city").
     */
    @SuppressWarnings("unchecked")
    private Object getFieldValue(String field, Map<String, Object> payload) {
        if (payload == null) {
            return null;
        }

        // Handle nested fields (e.g., "user.name")
        String[] parts = field.split("\\.");
        Object current = payload;

        for (String part : parts) {
            if (current == null) {
                return null;
            }
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return null;
            }
        }
        return current;
    }

    /**
     * Compares two values using the specified operator.
     */
    private static final int MAX_INPUT_LENGTH = 1000;

    @SuppressWarnings("unchecked")
    private boolean compare(Object actual, String op, Object expected) {
        // Handle null cases
        if (actual == null) {
            return switch (op) {
                case "==", "=" -> expected == null;
                case "!=", "<>" -> expected != null;
                default -> false;
            };
        }

        // Security check for string operations
        if (actual instanceof String && ((String) actual).length() > MAX_INPUT_LENGTH) {
            throw new InvalidConditionException(
                    "Input string too long for validation (Limit: " + MAX_INPUT_LENGTH + ")");
        }

        return switch (op) {
            case "==", "=" -> equals(actual, expected);
            case "!=", "<>" -> !equals(actual, expected);
            case ">" -> compareNumeric(actual, expected) > 0;
            case ">=" -> compareNumeric(actual, expected) >= 0;
            case "<" -> compareNumeric(actual, expected) < 0;
            case "<=" -> compareNumeric(actual, expected) <= 0;
            case "contains" -> containsCheck(actual, expected);
            case "startsWith" -> startsWithCheck(actual, expected);
            case "endsWith" -> endsWithCheck(actual, expected);
            case "in" -> inCheck(actual, expected);
            case "matches" -> regexCheck(actual, expected);
            case "dateBefore" -> dateCheck(actual, expected, "<");
            case "dateAfter" -> dateCheck(actual, expected, ">");
            default -> throw new InvalidConditionException("Unsupported operator: " + op);
        };
    }

    /**
     * Checks equality between two values, handling type conversions.
     */
    private boolean equals(Object actual, Object expected) {
        if (actual == null && expected == null)
            return true;
        if (actual == null || expected == null)
            return false;

        // Handle numeric comparisons
        if (actual instanceof Number && expected instanceof Number) {
            return ((Number) actual).doubleValue() == ((Number) expected).doubleValue();
        }

        // Handle string comparisons (case-insensitive option could be added)
        return actual.toString().equals(expected.toString());
    }

    /**
     * Compares two numeric values.
     */
    private int compareNumeric(Object actual, Object expected) {
        if (!(actual instanceof Number) || !(expected instanceof Number)) {
            throw new InvalidConditionException("Numeric comparison requires numbers");
        }
        return Double.compare(((Number) actual).doubleValue(), ((Number) expected).doubleValue());
    }

    /**
     * Checks if actual contains expected (for strings or collections).
     */
    @SuppressWarnings("unchecked")
    private boolean containsCheck(Object actual, Object expected) {
        if (actual instanceof String && expected instanceof String) {
            return ((String) actual).contains((String) expected);
        }
        if (actual instanceof Iterable) {
            for (Object item : (Iterable<?>) actual) {
                if (equals(item, expected))
                    return true;
            }
            return false;
        }
        throw new InvalidConditionException("Contains requires String or Iterable");
    }

    /**
     * Checks if string starts with expected value.
     */
    private boolean startsWithCheck(Object actual, Object expected) {
        return actual.toString().startsWith(expected.toString());
    }

    /**
     * Checks if string ends with expected value.
     */
    private boolean endsWithCheck(Object actual, Object expected) {
        return actual.toString().endsWith(expected.toString());
    }

    /**
     * Checks if actual value is in the expected collection.
     */
    @SuppressWarnings("unchecked")
    private boolean inCheck(Object actual, Object expected) {
        if (expected instanceof Iterable) {
            for (Object item : (Iterable<?>) expected) {
                if (equals(actual, item))
                    return true;
            }
            return false;
        }
        throw new InvalidConditionException("'in' operator requires Iterable expected value");
    }

    private boolean regexCheck(Object actual, Object expected) {
        return Pattern.matches(expected.toString(), actual.toString());
    }

    private boolean dateCheck(Object actual, Object expected, String op) {
        try {
            // Attempt to parse as LocalDateTime first (includes time)
            LocalDateTime actualDt = LocalDateTime.parse(actual.toString(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime expectedDt = LocalDateTime.parse(expected.toString(), DateTimeFormatter.ISO_DATE_TIME);

            return op.equals(">") ? actualDt.isAfter(expectedDt) : actualDt.isBefore(expectedDt);
        } catch (Exception e) {
            // Fallback to LocalDate if LocalDateTime parsing fails (date only)
            LocalDate actualD = LocalDate.parse(actual.toString());
            LocalDate expectedD = LocalDate.parse(expected.toString());
            return op.equals(">") ? actualD.isAfter(expectedD) : actualD.isBefore(expectedD);
        }
    }
}
