<template>
  <div id="app">
    <Menubar :model="items" /> <!-- Add model to pass menu items -->
    <router-view></router-view>
  </div>
</template>

<script>
import Menubar from 'primevue/menubar';

export default {
  name: 'App',
  components: {
    Menubar
  },
  data() {
    return {
      items: [
        {
          label: 'Manage Users',
          command: () => { this.$router.push('/') }
        },
        {
          label: 'Manage Publications',
          command: () => { this.$router.push('/publications') }
        }
      ]
    };
  }
};
</script>

<style>
* {
  font-family: Arial, sans-serif;
  font-weight: normal;
  margin: 0;
  padding: 0;
}

</style>