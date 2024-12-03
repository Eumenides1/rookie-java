<template>
<div class="dashboard">
    <h1>实时仪表盘</h1>
    <div class="metrics">
    <div class="metric">
        <h2>CPU 使用率</h2>
        <p>{{ data.cpu }}%</p>
    </div>
    <div class="metric">
        <h2>内存使用率</h2>
        <p>{{ data.memory }}%</p>
    </div>
    <div class="metric">
        <h2>网站流量</h2>
        <p>{{ data.traffic }} 次/秒</p>
    </div>
    </div>
</div>
</template>

<script>
export default {
data() {
    return {
    data: {
        cpu: 0,
        memory: 0,
        traffic: 0,
    },
    };
},
mounted() {
    const eventSource = new EventSource("http://localhost:8080/sse/dashboard");

    eventSource.addEventListener("dashboard-update", (event) => {
    const parsedData = JSON.parse(event.data);
    this.data = {
        cpu: parsedData.cpu.toFixed(2),
        memory: parsedData.memory.toFixed(2),
        traffic: parsedData.traffic.toFixed(2),
    };
    });

    eventSource.onerror = (error) => {
    console.error("SSE 连接错误:", error);
    eventSource.close();
    };
},
};
</script>

<style>
.dashboard {
font-family: Arial, sans-serif;
margin: 20px;
}

.metrics {
display: flex;
gap: 20px;
}

.metric {
padding: 10px;
border: 1px solid #ccc;
border-radius: 5px;
flex: 1;
text-align: center;
}

.metric h2 {
margin-bottom: 10px;
}

.metric p {
font-size: 24px;
color: #333;
}
</style>