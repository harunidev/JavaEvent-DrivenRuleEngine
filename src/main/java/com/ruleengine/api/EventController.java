package com.ruleengine.api;

import com.ruleengine.action.ActionDispatcher;
import com.ruleengine.engine.RuleEngine;
import com.ruleengine.model.ActionType;
import com.ruleengine.model.EvaluationResult;
import com.ruleengine.model.Event;
import com.ruleengine.model.Rule;
import com.ruleengine.repository.RuleRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST Controller for submitting events for evaluation.
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final RuleEngine ruleEngine;
    private final ActionDispatcher actionDispatcher;
    private final RuleRepository ruleRepository;

    public EventController(RuleEngine ruleEngine, ActionDispatcher actionDispatcher, RuleRepository ruleRepository) {
        this.ruleEngine = ruleEngine;
        this.actionDispatcher = actionDispatcher;
        this.ruleRepository = ruleRepository;
    }

    /**
     * Evaluates an event against all active rules and triggers actions.
     */
    @PostMapping
    public ResponseEntity<EvaluationResult> evaluateEvent(@Valid @RequestBody Event event) {
        logger.info("Received event for evaluation: {}", event.getType());

        // 1. Evaluate rules
        EvaluationResult result = ruleEngine.evaluate(event);

        // 2. Dispatch actions for matched rules
        if (result.isSuccess() && !result.getMatchedRules().isEmpty()) {
            for (String ruleName : result.getMatchedRules()) {
                Optional<Rule> ruleOpt = ruleRepository.getRule(ruleName);

                ruleOpt.ifPresent(rule -> {
                    if (rule.getThen() != null) {
                        ActionType actionType = rule.getThen().getAction();
                        if (actionType != null) {
                            actionDispatcher.dispatch(actionType, event, rule);
                        }
                    }
                });
            }
        }

        return ResponseEntity.ok(result);
    }
}
