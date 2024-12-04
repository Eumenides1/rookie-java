package com.rookie.stack.timeWheel;

/**
 * @author eumenides
 * @description
 * @date 2024/12/4
 */
public class Task {
    private String name;         // 任务名称
    private long executionTime;  // 执行时间（绝对时间戳）
    private Runnable action;     // 任务动作
    private long repeatInterval; // 重复间隔，0 表示不重复

    public Task(String name, long executionTime, Runnable action, long repeatInterval) {
        this.name = name;
        this.executionTime = executionTime;
        this.action = action;
        this.repeatInterval = repeatInterval;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    public void execute() {
        action.run();
    }

    public boolean isRepeating() {
        return repeatInterval > 0;
    }

    public void reschedule() {
        this.executionTime += repeatInterval;
    }

    @Override
    public String toString() {
        return name + " @ " + executionTime + (isRepeating() ? " (repeats)" : "");
    }
}