package com.ruleengine.repository;

import com.ruleengine.model.entity.RuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaRuleRepository extends JpaRepository<RuleEntity, String> {

    // Find all rules ordered by priority
    List<RuleEntity> findAllByOrderByPriorityAsc();

    // Find only enabled rules ordered by priority
    List<RuleEntity> findByEnabledTrueOrderByPriorityAsc();
}
