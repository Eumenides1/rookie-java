package com.rookie.stack.sse.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
@RestController
@RequestMapping("/sse")
@CrossOrigin(origins = "http://localhost:8081")
public class FriendActivityController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/friend-activity")
    public SseEmitter streamFriendActivity() {
        SseEmitter emitter = new SseEmitter(0L); // 无限超时
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

        Executors.newSingleThreadExecutor().submit(() -> {
            while (emitters.contains(emitter)) {
                try {
                    Thread.sleep(15000); // 每15秒发送一次心跳
                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                } catch (IOException e) {
                    emitters.remove(emitter);
                    System.out.println("心跳失败，移除连接：" + connectionId);
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        return emitter;
    }

    @PostMapping("/admin/post-activity")
    public void postActivity(@RequestBody String activity) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("friend-activity").data(activity));
            } catch (IOException e) {
                deadEmitters.add(emitter);
                System.out.println("移除失效连接：" + e.getMessage());
            }
        }

        emitters.removeAll(deadEmitters);
        System.out.println("当前有效连接数：" + emitters.size());
    }
}