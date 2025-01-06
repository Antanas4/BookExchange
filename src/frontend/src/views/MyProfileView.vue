<template>
  <ClientMenuBar v-if="CurrentUserRole === 'ROLE_CLIENT'"/>
  <AdminMenuBar v-if="CurrentUserRole === 'ROLE_ADMIN'"/>
  <div class="profile-container">
    <h1 class="page-header">{{ clientUsername }}</h1>
    <div class="container">
      <PublicationForm
          @update-my-publications="updatePublicationsList"
      />
      <MyPublicationsList
          :publications="myPublications"
      />

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
              <Button severity="success" label="Return" @click="handleReturnPublication(slotProps.data.id)"/>
            </template>
          </Column>
        </DataTable>
      </div>

    </div>
  </div>


</template>
<script setup>

import Button from 'primevue/button';
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import {onMounted, ref} from "vue";
import {
  getMyPublications,
  getMyBorrowedPublications,
  returnPublication,
} from "@/service/ManagePublicationsService";

import {getCurrentUserRoles} from "@/service/AuthenticationService";
import {useRoute} from "vue-router";
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import ClientMenuBar from "@/components/ClientMenuBar.vue";
import PublicationForm from "@/components/PublicationForm.vue";
import MyPublicationsList from "@/components/MyPublicationsList.vue";

const route = useRoute();
const clientUsername = route.params.clientUsername;
const CurrentUserRole = ref('');
const myPublications = ref([]);
const borrowedPublications = ref([]);

const updatePublicationsList = async () => {
  await getMyPublications(myPublications);
};

const handleGetMyBorrowedPublications = async () => {
  try {
    await getMyBorrowedPublications(borrowedPublications);
  } catch (error) {
    console.error('Error fetching borrowed publications:', error);
  }
};

const handleReturnPublication = async (publicationId) => {
  try {
    await returnPublication(publicationId);
  } catch (error) {
    console.error('Error fetching publications:', error);
  } finally {
    await getMyPublications();
    await handleGetMyBorrowedPublications();
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
      await updatePublicationsList();
      await handleGetMyBorrowedPublications();
    }
  } catch (error) {
    console.error('Error fetching roles:', error);
  }

});
</script>

<style>
.page-header {
  display: flex;
  justify-content: center;
  margin-top: 1em;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  min-height: 100vh;
}

.card-publication-list {
  margin: 1em 1em;
  padding-bottom: 2em;
}

.card-header {
  margin: 1em 1em;
}

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
</style>

