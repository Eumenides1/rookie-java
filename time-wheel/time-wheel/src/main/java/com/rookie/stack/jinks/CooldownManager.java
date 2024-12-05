package com.rookie.stack.jinks;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author eumenides
 * @description
 * @date 2024/12/5
 */
public class CooldownManager {
    private final Map<String, Skill> skills = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<WebSocketSession> sessions = new ArrayList<>();

    public CooldownManager() {
        skills.put("换枪！换枪！", new Skill("换枪！换枪！", 5));
        skills.put("震荡电磁波", new Skill("震荡电磁波", 8));
        skills.put("嚼火者手雷", new Skill("嚼火者手雷", 12));
        skills.put("超级死亡火箭！", new Skill("超级死亡火箭！", 90));
    }

    public void useSkill(String skillName) {
        Skill skill = skills.get(skillName);
        if (skill == null || !skill.isAvailable()) return;

        skill.useSkill();
        scheduler.schedule(() -> {
            skill.resetCooldown();
            broadcastSkillState(skillName, 0);
        }, skill.getCooldown(), TimeUnit.SECONDS);

        broadcastSkillState(skillName, skill.getCooldown());
    }

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    private void broadcastSkillState(String skillName, int cooldown) {
        String message = String.format("{\"skill\":\"%s\",\"cooldown\":%d}", skillName, cooldown);
        sessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
