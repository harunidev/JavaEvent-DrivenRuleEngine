package com.ruleengine.api;

import com.ruleengine.model.Rule;
import com.ruleengine.repository.RuleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing rules.
 */
@RestController
@RequestMapping("/rules")
public class RuleController {

    private final RuleRepository ruleRepository;

    public RuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /**
     * Adds or updates a rule.
     */
    @PostMapping
    public ResponseEntity<Rule> addRule(@Valid @RequestBody Rule rule) {
        ruleRepository.addRule(rule);
        return new ResponseEntity<>(rule, HttpStatus.CREATED);
    }

    /**
     * Gets all rules.
     */
    @GetMapping
    public ResponseEntity<List<Rule>> getAllRules() {
        return ResponseEntity.ok(ruleRepository.getAllRulesIncludingDisabled());
    }

    /**
     * Gets a specific rule by name.
     */
    @GetMapping("/{name}")
    public ResponseEntity<Rule> getRule(@PathVariable String name) {
        return ruleRepository.getRule(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a rule by name.
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteRule(@PathVariable String name) {
        if (ruleRepository.removeRule(name) != null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
