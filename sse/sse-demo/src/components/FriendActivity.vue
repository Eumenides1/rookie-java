<template>
<div id="app">
	<h1>SSE Demo</h1>
	<div class="controls">
	<button class="disconnect" @click="disconnectSSE">断开连接</button>
	<button class="clear" @click="clearMessages">清空消息</button>
	</div>
	<div id="messages">
	<div
		v-for="(message, index) in messages"
		:key="index"
		:class="['message', message.type]"
	>
		<strong>[{{ message.timestamp }}]</strong> {{ message.data }}
	</div>
	</div>
</div>
</template>

<script>
export default {
data() {
	return {
	eventSource: null,
	messages: [],
	};
},
methods: {
	connectSSE() {
	this.eventSource = new EventSource("http://localhost:8080/sse/friend-activity");

	this.eventSource.onmessage = (event) => {
		this.addMessage("message", event.data);
	};

	this.eventSource.addEventListener("heartbeat", (event) => {
		this.addMessage("heartbeat", event.data);
	});

	this.eventSource.addEventListener("friend-activity", (event) => {
		this.addMessage("friend-activity", event.data);
	});

	this.eventSource.onerror = (error) => {
		console.error("连接错误:", error);
		this.addMessage("error", "连接错误，请检查后端服务或网络状态");
		this.disconnectSSE();
	};
	},
	disconnectSSE() {
	if (this.eventSource) {
		this.eventSource.close();
		this.eventSource = null;
		this.addMessage("system", "SSE 连接已断开");
	}
	},
	addMessage(type, data) {
	const timestamp = new Date().toLocaleTimeString();
	this.messages.push({ type, data, timestamp });
	},
	clearMessages() {
	this.messages = [];
	},
},
mounted() {
	this.connectSSE();
},
};
</script>

<style>
body {
font-family: Arial, sans-serif;
margin: 0;
padding: 0;
}
#app {
max-width: 600px;
margin: 20px auto;
padding: 20px;
border: 1px solid #ddd;
border-radius: 5px;
background-color: #f9f9f9;
}
#messages {
margin-top: 20px;
max-height: 400px;
overflow-y: auto;
border: 1px solid #ccc;
border-radius: 5px;
padding: 10px;
background-color: #fff;
}
.message {
margin-bottom: 10px;
padding: 5px 10px;
border-left: 5px solid #007bff;
background-color: #f1f8ff;
}
.heartbeat {
border-left-color: #28a745;
}
.friend-activity {
border-left-color: #ffc107;
}
.system {
border-left-color: #6c757d;
}
.error {
border-left-color: #dc3545;
}
button {
margin-right: 10px;
padding: 10px 15px;
border: none;
border-radius: 3px;
cursor: pointer;
color: #fff;
}
.disconnect {
background-color: #dc3545;
}
.clear {
background-color: #6c757d;
}
</style>