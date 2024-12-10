<template>
  <Menubar :model="navLinks">
    <template #end>
      <Button label="Logout" class="p-button-danger" @click="handleLogout" />
    </template>
  </Menubar>
</template>

<script setup>
import { ref } from 'vue';
import Menubar from 'primevue/menubar';
import Button from 'primevue/button';
import axios from 'axios';
import { useRouter } from 'vue-router';

const navLinks = ref([
  {
    label: 'Manage Publications',
    command: () => {
      router.push('/publications');
    }
  },
  {
    label: 'Publications',
    command: () => {
      router.push('/publications/shop');
    }
  }
]);

const router = useRouter();

const handleLogout = async () => {
  try {
    await axios.post('/api/auth/logout');
    router.push('/auth');
  } catch (error) {
    console.error('Logout failed:', error);
  }
};

</script>

<style scoped>
/* Add your styles here if necessary */
</style>
