package com.ruleengine.action.handlers;

import com.ruleengine.action.ActionHandler;
import com.ruleengine.model.ActionType;
import com.ruleengine.model.Event;
import com.ruleengine.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BlockUserHandler implements ActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BlockUserHandler.class);

    @Override
    public void execute(Event event, Rule matchedRule) {
        String message = matchedRule.getThen().getMessage();
        if (message == null) {
            message = "User blocked by rule: " + matchedRule.getName();
        }

        logger.warn("EXECUTING BLOCK_USER ACTION: {}", message);
        logger.warn("Context - Event Type: {}, Payload: {}", event.getType(), event.getPayload());

        // In a real application, this would call a UserService or separate API
        // For this engine demo, we log the side-effect
    }

    @Override
    public ActionType getActionType() {
        return ActionType.BLOCK_USER;
    }
}
