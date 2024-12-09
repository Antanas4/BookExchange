<template>
  <div id="app">
    <Menubar :model="navLinks">
      <template #end>
        <Button label="Logout" class="p-button-danger" @click="handleLogout"/>
      </template>
    </Menubar>
    <router-view></router-view>
  </div>
</template>

<script>
import Menubar from 'primevue/menubar';
import Button from "primevue/button";
import axios from "axios";

export default {
  name: 'App',
  components: {
    Menubar,
    Button
  },
  data() {
    return {
      navLinks: [
        {
          label: 'Manage Users',
          command: () => {
            this.$router.push('/users')
          }
        },
        {
          label: 'Manage Publications',
          command: () => {
            this.$router.push('/publications')
          }
        },
        {
          label: 'Login',
          command: () => {
            this.$router.push('/auth')
          }
        },
        {
          label: 'Publications',
          command: () => {
            this.$router.push('/publications/shop')
          }
        }
      ]
    };
  },
  methods: {
    handleLogout() {
      axios.post('/api/auth/logout')
          .then(() => {
            this.$router.push('/auth');
          })
          .catch(error => {
            console.error('Logout failed:', error);
          });
    }
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