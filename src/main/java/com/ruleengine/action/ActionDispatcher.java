package com.ruleengine.action;

import com.ruleengine.model.ActionType;
import com.ruleengine.model.Event;
import com.ruleengine.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Dispatches actions to their corresponding handlers.
 * 
 * Maintains a registry of ActionHandlers and routes execution requests
 * based on the ActionType defined in the rule.
 */
@Component
public class ActionDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(ActionDispatcher.class);

    private final Map<ActionType, ActionHandler> handlers = new EnumMap<>(ActionType.class);

    /**
     * Constructor injection collects all beans implementing ActionHandler
     * and registers them in the map.
     */
    public ActionDispatcher(List<ActionHandler> handlerList) {
        for (ActionHandler handler : handlerList) {
            registerHandler(handler);
        }
    }

    /**
     * Registers a new action handler.
     */
    public void registerHandler(ActionHandler handler) {
        logger.info("Registering action handler for type: {}", handler.getActionType());
        handlers.put(handler.getActionType(), handler);
    }

    /**
     * Dispatches the action defined in the rule for the given event.
     * 
     * @param actionType The type of action to execute
     * @param event      The event that triggered the rule
     * @param rule       The matching rule containing action configuration
     */
    public void dispatch(ActionType actionType, Event event, Rule rule) {
        ActionHandler handler = handlers.get(actionType);

        if (handler != null) {
            try {
                logger.debug("Dispatching action {} for rule {}", actionType, rule.getName());
                handler.execute(event, rule);
            } catch (Exception e) {
                logger.error("Error executing action handler for type {}: {}", actionType, e.getMessage(), e);
            }
        } else {
            logger.warn("No handler registered for action type: {}", actionType);
        }
    }
}
