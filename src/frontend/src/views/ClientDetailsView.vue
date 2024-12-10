<template>
  <ClientMenuBar v-if="CurrentUserRole === 'ROLE_CLIENT'" />
  <AdminMenuBar v-if="CurrentUserRole === 'ROLE_ADMIN'" />
  <div v-if="client">
    <h1>{{ client.name }}</h1>
    <p><strong>Name:</strong> {{ client.name }}</p>
    <p><strong>Surname:</strong> {{ client.surname }}</p>
    <p><strong>Username:</strong> {{ client.username }}</p>
  </div>
  <div v-else>
    <p>Loading...</p>
  </div>
</template>

<script setup>
import {ref, onMounted} from "vue";
import {useRoute} from "vue-router";  // Import useRoute to access route params
import {getCurrentUserRoles} from "@/service/AuthenticationService";
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import ClientMenuBar from "@/components/ClientMenuBar.vue";
import axios from "axios";

const CurrentUserRole = ref('');
const client = ref(null);

const route = useRoute();
const ownerUsername = route.params.ownerUsername;

onMounted(async () => {
  try {
    const roles = await getCurrentUserRoles();
    if (roles.includes('ROLE_CLIENT')) {
      CurrentUserRole.value = 'ROLE_CLIENT';
    } else if (roles.includes('ROLE_ADMIN')) {
      CurrentUserRole.value = 'ROLE_ADMIN';
    }
    if (ownerUsername) {
      fetchClientDetails(ownerUsername);
    }
  } catch (error) {
    console.error('Error fetching roles:', error);
  }
});

const fetchClientDetails = async (ownerUsername) => {
  try {
    const response = await axios.get(`/api/${ownerUsername}`);
    client.value = response.data;
  } catch (error) {
    console.error('Error fetching client details:', error);
  }
};
</script>

<style scoped>
/* Styles */
</style>
