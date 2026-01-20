package com.ruleengine.action;

import com.ruleengine.model.ActionType;
import com.ruleengine.model.Event;
import com.ruleengine.model.Rule;

/**
 * Interface for handling specific action types.
 * 
 * Implementations should encapsulate the logic for executing a specific
 * side-effect (e.g., blocking a user, sending an email).
 */
public interface ActionHandler {

    /**
     * Executes the action for the given event and matched rule.
     * 
     * @param event       The event that triggered the rule
     * @param matchedRule The rule that triggered this action
     */
    void execute(Event event, Rule matchedRule);

    /**
     * Returns the type of action this handler supports.
     * 
     * @return The supported ActionType
     */
    ActionType getActionType();
}
