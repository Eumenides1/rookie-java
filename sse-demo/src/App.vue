<template>
  <div id="app">
    <h1>好友动态实时通知</h1>
    <FriendActivity />
    <h1>网站流量监控</h1>
    <Dashboard />
  </div>
</template>

<script>
import FriendActivity from "./components/FriendActivity.vue";
import Dashboard from "./components/Dashboard.vue";

export default {
  name: "App",
  components: {
    FriendActivity,
    Dashboard
  },
};
</script>

<style>
#app {
  font-family: Arial, sans-serif;
  text-align: center;
}
</style>