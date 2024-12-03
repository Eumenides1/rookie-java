<template>
  <div id="app">
    <h1>好友动态实时通知</h1>
    <FriendActivity />
  </div>
</template>

<script>
import FriendActivity from "./components/FriendActivity.vue";

export default {
  name: "App",
  components: {
    FriendActivity,
  },
};
</script>

<style>
#app {
  font-family: Arial, sans-serif;
  text-align: center;
}
</style>