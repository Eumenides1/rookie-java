package com.rookie.stack.sse.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * @author eumenides
 * @description
 * @date 2024/12/4
 */

@RestController
@RequestMapping("/sse")
@CrossOrigin(origins = "http://localhost:8081")
public class ChatController {

    private final List<Client> clients = new CopyOnWriteArrayList<>();

    static class Client {
        String id;
        SseEmitter emitter;

        public Client(String id, SseEmitter emitter) {
            this.id = id;
            this.emitter = emitter;
        }
    }

    @GetMapping("/connect")
    public SseEmitter connect() {
        // Infinite timeout
        SseEmitter emitter = new SseEmitter(0L);
        String clientId = UUID.randomUUID().toString();
        clients.add(new Client(clientId, emitter));

        System.out.println("新连接建立：" + clientId);

        emitter.onCompletion(() -> removeClient(clientId));
        emitter.onTimeout(() -> removeClient(clientId));
        emitter.onError(e -> removeClient(clientId));

        return emitter;
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody String message) {
        if (clients.isEmpty()) {
            System.out.println("无在线用户，消息丢弃：" + message);
            return;
        }

        Client recipient = getRandomClient();
        try {
            recipient.emitter.send(SseEmitter.event().name("new-message").data(message));
            System.out.println("消息发送给：" + recipient.id);
        } catch (IOException e) {
            System.out.println("消息发送失败：" + recipient.id);
            removeClient(recipient.id);
        }
    }

    @PostMapping("/reply")
    public void replyMessage(@RequestBody String reply) {
        if (clients.isEmpty()) {
            System.out.println("无在线用户，回复丢弃：" + reply);
            return;
        }

        Client recipient = getRandomClient();
        try {
            recipient.emitter.send(SseEmitter.event().name("reply").data(reply));
            System.out.println("回复发送给：" + recipient.id);
        } catch (IOException e) {
            System.out.println("回复发送失败：" + recipient.id);
            removeClient(recipient.id);
        }
    }

    private Client getRandomClient() {
        Random random = new Random();
        return clients.get(random.nextInt(clients.size()));
    }

    private void removeClient(String clientId) {
        clients.removeIf(client -> client.id.equals(clientId));
        System.out.println("连接移除：" + clientId + "，剩余连接数：" + clients.size());
    }
}
