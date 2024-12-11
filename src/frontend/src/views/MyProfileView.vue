<template>
  <ClientMenuBar v-if="CurrentUserRole === 'ROLE_CLIENT'"/>
  <AdminMenuBar v-if="CurrentUserRole === 'ROLE_ADMIN'"/>
  <div class="profile-container">
    <h1>{{ clientUsername }} profile</h1>

    <!-- My Publications Table -->
    <div class="card-publication-list">
      <h2 class="card-header">My Publications</h2>
      <DataTable :value="myPublications" tableStyle="min-width: 50rem">
        <Column field="author" header="Author"/>
        <Column field="title" header="Title"/>
        <Column field="price" header="Price"/>
        <Column field="publicationType" header="Publication Type"/>
        <Column field="status" header="Status"/>
        <Column header="Actions">
          <template #body="slotProps">
            <Button icon="pi pi-pencil" outlined rounded @click="openEditDialog(slotProps.data)"/>
            <Button icon="pi pi-trash" outlined rounded severity="danger" @click="deletePublication(slotProps.data.id)"/>
          </template>
        </Column>
      </DataTable>
    </div>

    <!-- Bought Publications Table -->
    <div class="card-publication-list">
      <h2 class="card-header">Bought Publications</h2>
      <DataTable :value="boughtPublications" tableStyle="min-width: 50rem">
        <Column field="author" header="Author"/>
        <Column field="title" header="Title"/>
        <Column field="price" header="Price"/>
        <Column field="publicationType" header="Publication Type"/>
        <Column field="ownerUsername" header="Owner Username"/>
      </DataTable>
    </div>

    <!-- Borrowed Publications Table -->
    <div class="card-publication-list">
      <h2 class="card-header">Borrowed Publications</h2>
      <DataTable :value="borrowedPublications" tableStyle="min-width: 50rem">
        <Column field="id" header="ID"/>
        <Column field="author" header="Author"/>
        <Column field="title" header="Title"/>
        <Column field="price" header="Price"/>
        <Column field="publicationType" header="Publication Type"/>
        <Column field="ownerUsername" header="Owner Username"/>
        <Column header="Actions">
          <template #body="slotProps">
            <Button icon="pi pi-eye" outlined rounded @click="viewPublication(slotProps.data)"/>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import axios from 'axios';
import {getCurrentUserRoles} from "@/service/AuthenticationService";
import {useRoute} from "vue-router";
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import ClientMenuBar from "@/components/ClientMenuBar.vue";

const route = useRoute();
const clientUsername = route.params.clientUsername;
const CurrentUserRole = ref('');
const myPublications = ref([]);
const boughtPublications = ref([]);
const borrowedPublications = ref([]);

const fetchMyPublications = async () => {
  try {
    const response = await axios.get('/api/publications/myPublications');
    myPublications.value = response.data;
  } catch (error) {
    console.error('Error fetching my publications:', error);
  }
};

const fetchBoughtPublications = async () => {
  try {
    const response = await axios.get('/api/publications/myPublications/bought');
    boughtPublications.value = response.data;
  } catch (error) {
    console.error('Error fetching bought publications:', error);
  }
};

const fetchBorrowedPublications = async () => {
  try {
    const response = await axios.get('/api/publications/myPublications/borrowed');
    borrowedPublications.value = response.data;
  } catch (error) {
    console.error('Error fetching borrowed publications:', error);
  }
};

onMounted(async () => {
  try {
    const roles = await getCurrentUserRoles();
    if (roles.includes('ROLE_CLIENT')) {
      CurrentUserRole.value = 'ROLE_CLIENT';
    } else if (roles.includes('ROLE_ADMIN')) {
      CurrentUserRole.value = 'ROLE_ADMIN';
    }
    if (clientUsername) {
      await fetchMyPublications();
      await fetchBoughtPublications();
      await fetchBorrowedPublications();
    }
  } catch (error) {
    console.error('Error fetching roles:', error);
  }

});

// Handle edit publication
const openEditDialog = (publication) => {
  console.log('Edit publication:', publication);
  // You can implement the logic for opening the edit dialog here
};

// Handle delete publication
const deletePublication = async (publicationId) => {
  try {
    await axios.delete(`/api/publications/${publicationId}`);
    // Re-fetch the publications after deletion
    fetchMyPublications();
  } catch (error) {
    console.error('Error deleting publication:', error);
  }
};

// Handle view publication
const viewPublication = (publication) => {
  console.log('View publication:', publication);
  // Implement logic to view the publication details
};
</script>

<style scoped>
.profile-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.card-publication-list {
  margin-bottom: 30px;
  width: 100%;
  max-width: 1200px;
}

.card-header {
  font-size: 24px;
  margin-bottom: 15px;
}

.p-button {
  margin-right: 10px;
}
</style>
