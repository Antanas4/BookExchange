<template>
  <Menubar :model="navLinks">
    <template #end>
      <Button label="Logout" class="p-button-danger" @click="handleLogout" />
    </template>
  </Menubar>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Menubar from 'primevue/menubar';
import Button from 'primevue/button';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref(''); // Holds the current user's username

// Define the navigation links
const navLinks = ref([
  // {
  //   label: 'Manage Publications',
  //   command: () => {
  //     router.push('/publications');
  //   }
  // },
  {
    label: 'Publications',
    command: () => {
      router.push('/publications/shop');
    }
  },
  {
    label: 'My Profile',
    command: () => {
      if (username.value) {
        router.push(`/myProfile/${username.value}`);
      } else {
        console.error('Username not found! Unable to navigate to profile.');
      }
    }
  }
]);

onMounted(async () => {
  try {
    const response = await axios.get('/api/currentUser');
    username.value = response.data;
  } catch (error) {
    console.error('Error fetching current username:', error);
  }
});

// Handle logout
const handleLogout = async () => {
  try {
    await axios.post('/api/auth/logout');
    username.value = '';
    router.push('/auth');
  } catch (error) {
    console.error('Logout failed:', error);
  }
};
</script>

<style scoped>
/* Add your styles here if necessary */
</style>
