package com.ruleengine.action.handlers;

import com.ruleengine.action.ActionHandler;
import com.ruleengine.model.ActionType;
import com.ruleengine.model.Event;
import com.ruleengine.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendAlertHandler implements ActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SendAlertHandler.class);

    @Override
    public void execute(Event event, Rule matchedRule) {
        String message = matchedRule.getThen().getMessage();

        logger.info(">>> ALERT SENT: [{}] - Triggered by rule '{}'",
                message != null ? message : "High Priority Alert",
                matchedRule.getName());
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SEND_ALERT;
    }
}
