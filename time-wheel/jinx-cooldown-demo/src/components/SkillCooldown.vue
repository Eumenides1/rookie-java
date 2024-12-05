<template>
    <div class="skill-cooldown">
        <h2>金克丝技能冷却状态</h2>
        <div class="skills">
        <div
            class="skill"
            v-for="skill in skills"
            :key="skill.name"
            :class="{ available: skill.cooldown === 0 }"
        >
        <h3>{{ skill.name }}</h3>
        <p v-if="skill.cooldown > 0">冷却中：{{ formatCooldown(skill.cooldown) }}</p>
        <p v-else>技能可用！</p>
        <!-- 冷却进度条 -->
        <div class="progress-bar">
            <div
                :style="{ width: calculateProgress(skill) + '%' }"
            ></div>
        </div>
        <button
            :disabled="skill.cooldown > 0"
            @click="useSkill(skill.name)"
        >
            {{ skill.cooldown > 0 ? "冷却中" : "释放技能" }}
        </button>
        </div>
    </div>
    </div>
</template>

<script>
export default {
    name: "SkillCooldown",
    data() {
        return {
            skills: [
                { name: "换枪！换枪！", cooldown: 0, maxCooldown: 5 },
                { name: "震荡电磁波", cooldown: 0, maxCooldown: 8 },
                { name: "嚼火者手雷", cooldown: 0, maxCooldown: 12 },
                { name: "超级死亡火箭！", cooldown: 0, maxCooldown: 90 },
            ],
            socket: null,
        };
    },
    mounted() {
        this.connectWebSocket();
    },
    methods: {
        connectWebSocket() {
        this.socket = new WebSocket("ws://localhost:8080/skill");
        this.socket.onmessage = (event) => {
            const { skill, cooldown } = JSON.parse(event.data);
            const skillObj = this.skills.find((s) => s.name === skill);
            if (skillObj) {
                skillObj.cooldown = cooldown;
                if (cooldown > 0) {
                    this.startCountdown(skillObj);
                }
            }
        };
},
        useSkill(skillName) {
            this.socket.send(skillName);
        },
        startCountdown(skill) {
            if (skill.countdownTimer) clearInterval(skill.countdownTimer);
            skill.countdownTimer = setInterval(() => {
                if (skill.cooldown > 0) {
                skill.cooldown -= 1;
                } else {
                    clearInterval(skill.countdownTimer);
                }
            }, 1000); // 每秒更新一次冷却时间
        },
        formatCooldown(seconds) {
            const minutes = Math.floor(seconds / 60);
            const secs = seconds % 60;
            return minutes > 0 ? `${minutes} 分 ${secs} 秒` : `${secs} 秒`;
        },
        calculateProgress(skill) {
            if (skill.maxCooldown && skill.cooldown > 0) {
                return ((skill.maxCooldown - skill.cooldown) / skill.maxCooldown) * 100;
            }
            return 0; // 初始进度为 0
        },
    },
};
</script>
<style scoped>
.skill-cooldown {
    text-align: center;
    font-family: Arial, sans-serif;
    margin-top: 20px;
}

.skills {
    display: flex;
    flex-wrap: nowrap; /* 禁止换行 */
    overflow-x: auto; /* 横向滚动支持 */
    gap: 20px;
    padding: 10px;
}

.skill {
    border: 1px solid #ddd;
    border-radius: 5px;
    padding: 15px;
    width: 200px; /* 固定卡片宽度 */
    background-color: #f5f5f5;
    text-align: center;
    transition: background-color 0.3s ease;
}

.skill.available {
  background-color: #d4f4dd; /* 冷却完成时的背景色 */
}

button {
    margin-top: 10px;
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    background-color: #007bff;
    color: #fff;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

button:disabled {
    background-color: #ddd;
    cursor: not-allowed;
}

button:not(:disabled):hover {
    background-color: #0056b3;
}

/* 冷却进度条 */
.progress-bar {
    height: 10px;
    background-color: #ddd;
    border-radius: 5px;
    overflow: hidden;
    margin-top: 10px;
    position: relative;
}

.progress-bar > div {
    height: 100%;
    background-color: #007bff;
    width: 0%; /* 初始进度 */
    transition: width 0.1s linear;
}
</style>