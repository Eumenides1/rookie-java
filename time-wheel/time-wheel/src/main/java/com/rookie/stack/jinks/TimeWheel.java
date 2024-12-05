package com.rookie.stack.jinks;

import java.util.*;

/**
 * @author eumenides
 * @description
 * @date 2024/12/5
 */
public class TimeWheel {
    private int size;                     // 槽的数量
    private long tickDuration;            // 每槽的时间间隔（毫秒）
    private List<Set<Runnable>> slots;    // 槽列表，每个槽存储一组任务
    private int currentIndex = 0;         // 当前指针位置

    public TimeWheel(int size, long tickDuration) {
        this.size = size;
        this.tickDuration = tickDuration;
        this.slots = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            slots.add(new HashSet<>());
        }
    }

    // 添加任务
    public void addTask(Runnable task, int delayInSeconds) {
        int ticks = delayInSeconds / (int) (tickDuration / 1000);
        int index = (currentIndex + ticks) % size;
        slots.get(index).add(task);
        System.out.println("任务已添加到槽位：" + index + "，延迟 " + delayInSeconds + " 秒");
    }

    // 时间推进
    public void advanceTime() {
        System.out.println("时间轮当前指针位置：" + currentIndex);
        Set<Runnable> currentSlotTasks = slots.get(currentIndex);

        // 执行当前槽中的任务
        Iterator<Runnable> iterator = currentSlotTasks.iterator();
        while (iterator.hasNext()) {
            Runnable task = iterator.next();
            task.run(); // 执行任务
            iterator.remove(); // 移除已执行的任务
        }

        // 指针前进
        currentIndex = (currentIndex + 1) % size;
    }
}
