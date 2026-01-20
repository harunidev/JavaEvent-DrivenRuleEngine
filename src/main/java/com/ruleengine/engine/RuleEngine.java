package com.ruleengine.engine;

import com.ruleengine.evaluator.ConditionEvaluator;
import com.ruleengine.evaluator.EvaluationDetail;
import com.ruleengine.model.EvaluationResult;
import com.ruleengine.model.Event;
import com.ruleengine.model.Rule;
import com.ruleengine.model.RuleTrace;
import com.ruleengine.repository.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The core rule engine that evaluates events against registered rules.
 * 
 * Features:
 * - Evaluates rules in priority order (lower priority value = runs first)
 * - Supports short-circuit mode (stop on first match)
 * - Stateless operation (v1)
 * - Thread-safe
 */
@Component
public class RuleEngine {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngine.class);

    private final RuleRepository ruleRepository;
    private final ConditionEvaluator conditionEvaluator;

    public RuleEngine(RuleRepository ruleRepository, ConditionEvaluator conditionEvaluator) {
        this.ruleRepository = ruleRepository;
        this.conditionEvaluator = conditionEvaluator;
    }

    /**
     * Evaluates an event against all registered rules.
     * All matching rules are collected.
     * 
     * @param event The event to evaluate
     * @return The evaluation result containing matched rules and actions
     */
    public EvaluationResult evaluate(Event event) {
        return evaluate(event, false);
    }

    /**
     * Evaluates an event against registered rules.
     * 
     * @param event            The event to evaluate
     * @param stopOnFirstMatch If true, stops after the first matching rule
     * @return The evaluation result
     */
    public EvaluationResult evaluate(Event event, boolean stopOnFirstMatch) {
        long startTime = System.currentTimeMillis();

        logger.info("Evaluating event: type={}", event.getType());

        EvaluationResult result = new EvaluationResult(event.getType());

        try {
            List<Rule> rules = ruleRepository.getAllRules();
            result.setTotalRulesEvaluated(rules.size());

            logger.debug("Found {} active rules to evaluate", rules.size());

            for (Rule rule : rules) {
                try {
                    boolean matches = evaluateRule(rule, event, result);

                    if (matches) {
                        logger.info("Rule '{}' matched event type '{}'",
                                rule.getName(), event.getType());
                        result.addMatch(rule);

                        if (stopOnFirstMatch) {
                            logger.debug("Short-circuit: stopping after first match");
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error evaluating rule '{}': {}", rule.getName(), e.getMessage());
                    // Continue with next rule
                }
            }

        } catch (Exception e) {
            logger.error("Error during rule evaluation: {}", e.getMessage());
            return EvaluationResult.error(event.getType(), e.getMessage());
        }

        long executionTime = System.currentTimeMillis() - startTime;
        result.setExecutionTimeMs(executionTime);

        logger.info("Evaluation complete: {} rules matched in {}ms",
                result.getMatchedRules().size(), executionTime);

        return result;
    }

    /**
     * Evaluates a single rule against an event.
     * 
     * @param rule  The rule to evaluate
     * @param event The event to evaluate against
     * @return true if the rule's condition is satisfied
     */
    private boolean evaluateRule(Rule rule, Event event, EvaluationResult result) {
        if (rule.getWhen() == null) {
            // Rule with no condition always matches
            result.addTrace(new RuleTrace(rule.getName(), true));
            return true;
        }

        EvaluationDetail detail = conditionEvaluator.evaluate(rule.getWhen(), event.getPayload());

        if (detail.isMatched()) {
            result.addTrace(new RuleTrace(rule.getName(), true));
            return true;
        } else {
            result.addTrace(new RuleTrace(rule.getName(), false, detail.getReason()));
            return false;
        }
    }

    /**
     * Evaluates a single event against a specific rule by name.
     * 
     * @param event    The event to evaluate
     * @param ruleName The name of the rule to evaluate against
     * @return The evaluation result
     */
    public EvaluationResult evaluateWithRule(Event event, String ruleName) {
        long startTime = System.currentTimeMillis();
        EvaluationResult result = new EvaluationResult(event.getType());

        ruleRepository.getRule(ruleName).ifPresentOrElse(
                rule -> {
                    result.setTotalRulesEvaluated(1);
                    if (rule.isEnabled() && evaluateRule(rule, event, result)) {
                        result.addMatch(rule);
                    }
                },
                () -> {
                    result.setSuccess(false);
                    result.setErrorMessage("Rule not found: " + ruleName);
                });

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        return result;
    }
}
