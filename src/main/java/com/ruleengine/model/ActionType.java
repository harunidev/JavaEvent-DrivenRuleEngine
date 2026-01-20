package com.ruleengine.model;

/**
 * Enum representing the types of actions that can be triggered by rules.
 * 
 * Each action type corresponds to a specific handler that executes
 * the appropriate side-effect when a rule matches.
 */
public enum ActionType {

    /**
     * Blocks the user from performing the action.
     * Use case: Suspicious login attempts, fraud detection.
     */
    BLOCK_USER,

    /**
     * Sends an alert notification to administrators.
     * Use case: Security incidents, threshold breaches.
     */
    SEND_ALERT,

    /**
     * Logs the event for audit/monitoring purposes.
     * Use case: Compliance logging, activity tracking.
     */
    LOG_EVENT,

    /**
     * Sends a notification to the user.
     * Use case: Welcome messages, status updates.
     */
    NOTIFY,

    /**
     * Approves the action/request.
     * Use case: Auto-approval workflows.
     */
    APPROVE,

    /**
     * Rejects the action/request.
     * Use case: Policy violations, quota exceeded.
     */
    REJECT,

    /**
     * Flags the event for manual review.
     * Use case: Edge cases, uncertain scenarios.
     */
    FLAG_FOR_REVIEW
}
