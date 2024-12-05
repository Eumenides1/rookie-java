package com.rookie.stack.jinks;

/**
 * @author eumenides
 * @description
 * @date 2024/12/5
 */
public class Skill {
    private String name;       // 技能名称
    private int cooldown;      // 冷却时间（秒）
    private boolean isAvailable; // 技能是否可用

    public Skill(String name, int cooldown) {
        this.name = name;
        this.cooldown = cooldown;
        this.isAvailable = true; // 初始状态为可用
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void useSkill() {
        if (!isAvailable) {
            System.out.println("技能 " + name + " 仍在冷却中！");
            return;
        }
        System.out.println("释放技能：" + name);
        isAvailable = false; // 设置技能为不可用
    }

    public void resetCooldown() {
        isAvailable = true; // 冷却结束，技能可用
        System.out.println("技能 " + name + " 冷却完成，可以再次释放！");
    }
}
