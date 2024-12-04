<template>
    <div>
        <h1>已读乱回</h1>
        <div class="messages">
            <div v-for="msg in messages" :key="msg.id" class="message">
                <p>{{ msg.text }}</p>
            </div>
        </div>
        <input v-model="input" placeholder="输入消息或回复..." />
        <button @click="sendMessage">发送消息</button>
        <button @click="sendReply">发送回复</button>
    </div>
</template>
<script>
export default {
    data() {
        return {
            input: "",
            messages: [],
            eventSource: null,
        };
    },
    mounted() {
        this.eventSource = new EventSource("http://localhost:8080/sse/connect");
    
        this.eventSource.addEventListener("new-message", (event) => {
            this.messages.push({ id: Date.now(), text: "收到消息: " + event.data });
        });

        this.eventSource.addEventListener("reply", (event) => {
            this.messages.push({ id: Date.now(), text: "收到回复: " + event.data });
        });

        this.eventSource.onerror = (error) => {
            console.error("连接出错:", error);
        };
    },
    methods: {
        sendMessage() {
            fetch("http://localhost:8080/sse/send", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(this.input),
            });
            this.input = "";
        },
        sendReply() {
        fetch("http://localhost:8080/sse/reply", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(this.input),
        });
        this.input = "";
        },
    },
};
</script>

<style>
body {
font-family: Arial, sans-serif;
}
.messages {
max-height: 300px;
overflow-y: auto;
border: 1px solid #ccc;
padding: 10px;
}
.message {
padding: 5px;
border-bottom: 1px solid #eee;
}
</style>