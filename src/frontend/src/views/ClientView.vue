
<template>
  <ClientMenuBar v-if="CurrentUserRole === 'ROLE_CLIENT'"/>
  <AdminMenuBar v-if="CurrentUserRole === 'ROLE_ADMIN'"/>
</template>

<script setup>
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import ClientMenuBar from "@/components/ClientMenuBar.vue";
import {onMounted, ref} from "vue";
import {getCurrentUserRoles} from "@/service/AuthenticationService";

const CurrentUserRole = ref('');

onMounted(async () => {
  try {
    const roles = await getCurrentUserRoles();
    if (roles.includes('ROLE_CLIENT')) {
      CurrentUserRole.value = 'ROLE_CLIENT';
    } else if (roles.includes('ROLE_ADMIN')) {
      CurrentUserRole.value = 'ROLE_ADMIN';
    }
  } catch (error) {
    console.error('Error fetching data:', error);
  }
});

</script>

<style scoped>

</style>