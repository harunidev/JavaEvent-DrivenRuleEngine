package com.ruleengine.repository;

import com.ruleengine.model.Rule;
import com.ruleengine.model.entity.RuleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service/Repository layer that acts as a bridge between the Rule Engine and
 * JPA.
 * 
 * Replaces the previous in-memory Map implementation with H2-backed
 * persistence.
 */
@Service
@Primary
public class RuleRepository {

    private static final Logger logger = LoggerFactory.getLogger(RuleRepository.class);

    private final JpaRuleRepository jpaRepository;

    public RuleRepository(JpaRuleRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public Rule addRule(Rule rule) {
        logger.info("Persisting rule: {}", rule.getName());
        RuleEntity entity = RuleEntity.fromDomain(rule);
        jpaRepository.save(entity);
        return rule;
    }

    public Rule removeRule(String ruleName) {
        logger.info("Removing rule: {}", ruleName);
        Optional<RuleEntity> entityOpt = jpaRepository.findById(ruleName);
        if (entityOpt.isPresent()) {
            Rule rule = entityOpt.get().toDomain();
            jpaRepository.deleteById(ruleName);
            return rule;
        }
        return null;
    }

    public Optional<Rule> getRule(String name) {
        return jpaRepository.findById(name).map(RuleEntity::toDomain);
    }

    public List<Rule> getAllRules() {
        return jpaRepository.findByEnabledTrueOrderByPriorityAsc().stream()
                .map(RuleEntity::toDomain)
                .collect(Collectors.toList());
    }

    public List<Rule> getAllRulesIncludingDisabled() {
        return jpaRepository.findAllByOrderByPriorityAsc().stream()
                .map(RuleEntity::toDomain)
                .collect(Collectors.toList());
    }

    public boolean exists(String ruleName) {
        return jpaRepository.existsById(ruleName);
    }

    public long count() {
        return jpaRepository.count();
    }

    public void clear() {
        logger.warn("Clearing all rules from database");
        jpaRepository.deleteAll();
    }
}
