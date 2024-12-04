<template>
  <div id="app">
    <h1>好友动态实时通知</h1>
    <FriendActivity />
    <h1>网站流量监控</h1>
    <Dashboard />
    <h1>已读乱回</h1>
    <Chat />
  </div>
</template>

<script>
import FriendActivity from "./components/FriendActivity.vue";
import Dashboard from "./components/Dashboard.vue";
import Chat from "./components/Chat.vue";

export default {
  name: "App",
  components: {
    FriendActivity,
    Dashboard,
    Chat
  },
};
</script>

<style>
#app {
  font-family: Arial, sans-serif;
  text-align: center;
}
</style>