package com.rookie.stack.timeWheel;

import java.util.*;

/**
 * @author eumenides
 * @description
 * @date 2024/12/4
 */
class TimeWheel {
    private int size;                     // 槽的数量
    private long tickDuration;            // 每个槽代表的时间间隔
    private List<Set<Task>> slots;        // 槽列表，每个槽存储一组任务
    private int currentIndex = 0;         // 当前指针位置
    private long startTime;               // 时间轮的起始时间

    public TimeWheel(int size, long tickDuration) {
        this.size = size;
        this.tickDuration = tickDuration;
        this.slots = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            slots.add(new HashSet<>());
        }
        this.startTime = System.currentTimeMillis();
    }

    // 添加任务
    public void addTask(Task task) {
        long delay = task.getExecutionTime() - System.currentTimeMillis();
        if (delay < 0) {
            throw new IllegalArgumentException("任务时间不能是过去的时间");
        }

        int index = (int) ((currentIndex + delay / tickDuration) % size);
        slots.get(index).add(task);
        System.out.println("任务添加成功：" + task + " -> 槽位 " + index);
    }

    // 时间推进
    public void advanceTime() {
        System.out.println("当前时间槽：" + currentIndex + "，槽中任务：" + slots.get(currentIndex));
        Set<Task> currentSlotTasks = slots.get(currentIndex);
        Iterator<Task> iterator = currentSlotTasks.iterator();

        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getExecutionTime() <= System.currentTimeMillis()) {
                task.execute();
                iterator.remove();

                // 如果是重复任务，重新调度
                if (task.isRepeating()) {
                    task.reschedule();
                    addTask(task);
                }
            }
        }

        currentIndex = (currentIndex + 1) % size;
    }
}
