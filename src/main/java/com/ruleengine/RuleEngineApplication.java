package com.ruleengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Java Rule Engine application.
 * 
 * This is a lightweight, event-driven rule engine that allows
 * defining decision logic as JSON-based rules instead of hard-coded conditions.
 */
@SpringBootApplication
public class RuleEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleEngineApplication.class, args);
    }
}
