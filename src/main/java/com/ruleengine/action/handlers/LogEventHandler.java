package com.ruleengine.action.handlers;

import com.ruleengine.action.ActionHandler;
import com.ruleengine.model.ActionType;
import com.ruleengine.model.Event;
import com.ruleengine.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogEventHandler implements ActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogEventHandler.class);

    @Override
    public void execute(Event event, Rule matchedRule) {
        logger.info("AUDIT LOG: Rule '{}' matched event '{}'. Data: {}",
                matchedRule.getName(), event.getType(), event.getPayload());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.LOG_EVENT;
    }
}
