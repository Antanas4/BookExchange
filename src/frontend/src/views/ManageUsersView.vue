<template>
  <AdminMenuBar/>
  <h1 class="page-header">Manage Users</h1>
  <div class="container">
    <UserForm @user-created="handleGetUsers" :create-user="createUser" />
    <UserList :users="users" @edit-user="openEditDialog" @delete-user="handleDeleteUser" />
  </div>

  <Dialog v-model:visible="showDialog" header="Warning" :modal="true" :closable="false">
    <p>{{ dialogMessage }}</p>
    <Button label="OK" @click="showDialog = false"/>
  </Dialog>

  <EditUserDialog
      v-model:visible="editDialogVisible"
      :user="editingUser"
      :update-user="updateUser"
      @user-updated="handleGetUsers"
  />
</template>

<script setup>
import {ref} from 'vue';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import {createUser, deleteUser, getUsers, updateUser} from '@/service/ManageUsersService';
import AdminMenuBar from '@/components/AdminMenuBar.vue';
import UserForm from '@/components/UserForm.vue';
import UserList from '@/components/UserList.vue'
import EditUserDialog from '@/components/EditUserDialog.vue';

const users = ref([]);
const showDialog = ref(false);
const dialogMessage = ref('');
const editDialogVisible = ref(false);
const editingUser = ref({});

const handleGetUsers = async () => {
  try {
    users.value = await getUsers();
  } catch (error) {
    dialogMessage.value = error.message;
    showDialog.value = true;
  }
};

const handleDeleteUser = async (usernameToDelete) => {
  try {
    await deleteUser(usernameToDelete);
    dialogMessage.value = "User deleted successfully.";
    showDialog.value = true;
  } catch (error) {
    dialogMessage.value = error.message;
    showDialog.value = true;
  } finally {
    await handleGetUsers();
  }
};

const openEditDialog = (user) => {
  editingUser.value = {...user};
  editDialogVisible.value = true;
};

handleGetUsers();
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
</style>