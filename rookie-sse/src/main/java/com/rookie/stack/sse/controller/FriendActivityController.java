package com.rookie.stack.sse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author eumenides
 */
@RestController
@RequestMapping("/sse")
@CrossOrigin(origins = "http://localhost:8081") // 设置允许跨域的前端地址
public class FriendActivityController {

    private static final Logger logger = LoggerFactory.getLogger(FriendActivityController.class);

    // 使用线程安全的 CopyOnWriteArrayList 存储连接
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * SSE 连接接口，用于订阅好友活动消息
     *
     * @return 返回 SseEmitter 实例，代表一个 SSE 连接
     */
    @GetMapping("/friend-activity")
    public SseEmitter streamFriendActivity() {
        // 创建一个无限超时的 SSE 连接
        SseEmitter emitter = new SseEmitter(0L);
        // 生成唯一连接 ID
        String connectionId = UUID.randomUUID().toString();
        // 添加到连接列表
        emitters.add(emitter);
        logger.info("新连接建立：{}，当前总连接数：{}", connectionId, emitters.size());
        // 注册回调函数，当连接关闭、超时或出错时移除连接
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            logger.info("连接完成：{}，当前剩余连接数：{}", connectionId, emitters.size());
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            logger.info("连接超时：{}，当前剩余连接数：{}", connectionId, emitters.size());
        });
        emitter.onError((e) -> {
            emitters.remove(emitter);
            logger.warn("连接出错：{}，错误：{}", connectionId, e.getMessage());
        });

        // 定时发送心跳消息
        Executors.newSingleThreadExecutor().submit(() -> {
            while (emitters.contains(emitter)) {
                try {
                    Thread.sleep(15000); // 每 15 秒发送一次心跳
                    emitter.send(SseEmitter.event().name("heartbeat").data("ping")); // 发送心跳消息
                } catch (IOException e) {
                    emitters.remove(emitter);
                    logger.warn("心跳失败，移除连接：{}", connectionId);
                    break; // 如果发送失败，则移除连接并退出循环
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break; // 如果线程被中断，则退出循环
                }
            }
        });
        return emitter;
    }

    /**
     * 管理员接口，用于推送好友活动消息
     *
     * @param activity 活动消息内容
     */
    @PostMapping("/admin/post-activity")
    public void postActivity(@RequestBody String activity) {
        // 存储失效的连接
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                // 推送消息
                emitter.send(SseEmitter.event().name("friend-activity").data(activity));
            } catch (IOException e) {
                // 将失效连接添加到列表
                deadEmitters.add(emitter);
                logger.warn("移除失效连接：{}", e.getMessage());
            }
        }
        // 移除所有失效连接
        emitters.removeAll(deadEmitters);
        logger.info("当前有效连接数：{}", emitters.size());
    }
}