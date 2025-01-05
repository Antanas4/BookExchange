<template>
  <div class="card-user-list">
    <h2 class="card-header">User List</h2>
    <DataTable :value="users" tableStyle="min-width: 50rem">
      <Column field="name" header="Name"/>
      <Column field="surname" header="Surname"/>
      <Column field="username" header="Username"/>
      <Column field="address" header="Address"/>
      <Column field="dateOfBirth" header="Date of Birth"/>
      <Column field="adminLevel" header="Admin Level"/>
      <Column header="Actions">
        <template #body="slotProps">
          <Button icon="pi pi-pencil" outlined rounded @click="$emit('editUser', slotProps.data)" class="mr-2"/>
          <Button icon="pi pi-trash" outlined rounded severity="danger"
                  @click="$emit('deleteUser', slotProps.data.username)"/>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script setup>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import { defineEmits, defineProps } from 'vue';

defineProps(['users']);
defineEmits(['editUser', 'deleteUser']);
</script>

<style scoped>
.card-user-list {
  margin: 1em 1em;
  padding-bottom: 2em;
}

.card-header {
  margin: 1em 1em;
}
</style>