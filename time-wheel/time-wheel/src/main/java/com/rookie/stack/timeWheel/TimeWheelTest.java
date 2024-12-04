package com.rookie.stack.timeWheel;

/**
 * @author eumenides
 * @description
 * @date 2024/12/4
 */
public class TimeWheelTest {
    public static void main(String[] args) throws InterruptedException {
        TimeWheel timeWheel = new TimeWheel(8, 1000); // 8个槽，每槽1秒

        // 添加单次任务
        timeWheel.addTask(new Task("单次任务1", System.currentTimeMillis() + 3000,
                () -> System.out.println("单次任务1执行"), 0));

        // 添加周期性任务
        timeWheel.addTask(new Task("周期任务1", System.currentTimeMillis() + 2000,
                () -> System.out.println("周期任务1执行"), 4000));

        // 模拟时间推进
        for (int i = 0; i < 12; i++) {
            Thread.sleep(1000); // 每秒钟推进一次
            timeWheel.advanceTime();
        }
    }
}
