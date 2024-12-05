<template>
  <div id="app">
    <SkillCooldown />
  </div>
</template>

<script>
import SkillCooldown from "./components/SkillCooldown.vue";

export default {
  name: "App",
  components: {
    SkillCooldown,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>