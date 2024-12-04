package com.rookie.stack.sse.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@RestController
@RequestMapping("/sse")
@CrossOrigin(origins = "http://localhost:8081")
public class DashboardController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @GetMapping("/dashboard")
    public SseEmitter streamDashboardData() {
        // 创建一个无限超时的 SSE 连接
        SseEmitter emitter = new SseEmitter(0L);
        String connectionId = UUID.randomUUID().toString();
        emitters.add(emitter);

        System.out.println("新连接建立：" + connectionId + "，当前总连接数：" + emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            System.out.println("连接完成：" + connectionId + "，当前剩余连接数：" + emitters.size());
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            System.out.println("连接超时：" + connectionId + "，当前剩余连接数：" + emitters.size());
        });
        emitter.onError((e) -> {
            emitters.remove(emitter);
            System.out.println("连接出错：" + connectionId + "，错误：" + e.getMessage());
        });

        // 模拟监控数据的实时推送
        scheduler.scheduleAtFixedRate(() -> {
            if (!emitters.contains(emitter)) {
                return; // 如果连接已经关闭，则跳过
            }
            try {
                String mockData = generateMockDashboardData();
                emitter.send(SseEmitter.event().name("dashboard-update").data(mockData));
            } catch (IOException e) {
                emitters.remove(emitter);
                System.out.println("数据发送失败，移除连接：" + connectionId);
            }
        }, 0, 5, TimeUnit.SECONDS); // 每5秒发送一次数据

        return emitter;
    }

    // 模拟生成监控数据
    private String generateMockDashboardData() {
        return "{ \"cpu\": " + (50 + Math.random() * 30) + ", " +
                "\"memory\": " + (30 + Math.random() * 40) + ", " +
                "\"traffic\": " + (100 + Math.random() * 200) + " }";
    }
}
