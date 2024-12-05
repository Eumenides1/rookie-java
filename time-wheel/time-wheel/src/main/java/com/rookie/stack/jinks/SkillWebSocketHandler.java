package com.rookie.stack.jinks;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author eumenides
 * @description
 * @date 2024/12/5
 */
public class SkillWebSocketHandler extends TextWebSocketHandler {
    private final CooldownManager cooldownManager = new CooldownManager();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        cooldownManager.addSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String skillName = message.getPayload();
        cooldownManager.useSkill(skillName);
    }
}
