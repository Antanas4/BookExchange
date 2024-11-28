<template>
  <h1 class="page-header">Manage Users</h1>
  <div class="container">
    <div class="form">
      <div class="flex items-center">
        <RadioButton v-model="userType" inputId="is-client" name="userType" value="Client"/>
        <label for="is-client" class="ml-2">Client</label>
      </div>
      <div class="flex items-center">
        <RadioButton v-model="userType" inputId="is-admin" name="userType" value="Admin"/>
        <label for="is-admin" class="ml-2">Admin</label>
      </div>

      <InputText id="name" v-model="name" placeholder="Enter Name" @input="validateField('name')"/>
      <p v-if="warningMessages.name" class="warning-message">{{ warningMessages.name }}</p>

      <InputText id="surname" v-model="surname" placeholder="Enter Surname" @input="validateField('surname')"/>
      <p v-if="warningMessages.surname" class="warning-message">{{ warningMessages.surname }}</p>

      <InputText id="username" v-model="username" placeholder="Enter Username" @input="validateField('username')"/>
      <p v-if="warningMessages.username" class="warning-message">{{ warningMessages.username }}</p>

      <Password id="password" v-model="password" :feedback="false" placeholder="Enter Password"/>
      <DatePicker v-if="userType === 'Client'" v-model="dateOfBirth" showIcon fluid iconDisplay="input"
                  placeholder="Enter Date Of Birth"
                  :disabled="userType === 'Admin'"/>
      <InputText id="address" v-if="userType === 'Client'" v-model="address" placeholder="Enter Address"
                 :disabled="userType === 'Admin'"/>

      <AutoComplete v-if="userType === 'Admin'" v-model="adminLevel" dropdown :suggestions="adminLevelOptions"
                    placeholder="Select Admin Level" @complete="searchAdminLevel"/>

      <Button label="Create" severity="success" @click="handleCreateUser" :disabled="!isFormValid"/>
    </div>
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
            <Button icon="pi pi-pencil" outlined rounded @click="openEditDialog(slotProps.data)" class="mr-2"/>
            <Button icon="pi pi-trash" outlined rounded severity="danger"
                    @click="handleDeleteUser(slotProps.data.username)"/>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>

  <Dialog v-model:visible="showDialog" header="Warning" :modal="true" :closable="false">
    <p>{{ dialogMessage }}</p>
    <Button label="OK" @click="showDialog = false"/>
  </Dialog>

  <Dialog v-model:visible="editDialogVisible" modal header="Edit User" class="edit-user-dialog">
    <div class="dialog-input-group">
      <label for="name" class="dialog-label">Name</label>
      <InputText id="name" v-model="editingUser.name" class="dialog-input" @input="validateEditField('name')"/>
      <p v-if="editWarningMessages.name" class="warning-message">{{ editWarningMessages.name }}</p>
    </div>

    <div class="dialog-input-group">
      <label for="surname" class="dialog-label">Surname</label>
      <InputText id="surname" v-model="editingUser.surname" class="dialog-input"
                 @input="validateEditField('surname')"/>
      <p v-if="editWarningMessages.surname" class="warning-message">{{ editWarningMessages.surname }}</p>
    </div>

    <div class="dialog-input-group">
      <label for="username" class="dialog-label">Username</label>
      <InputText id="username" v-model="editingUser.username" class="dialog-input" disabled/>
    </div>

    <div class="dialog-input-group" v-if="editingUser.userType === 'Client'">
      <label for="address" class="dialog-label">Address</label>
      <InputText id="address" v-model="editingUser.address" class="dialog-input"/>
    </div>

    <div class="dialog-input-group" v-if="editingUser.userType === 'Client'">
      <label for="dateOfBirth" class="dialog-label">Date of Birth</label>
      <DatePicker id="dateOfBirth" v-model="editingUser.dateOfBirth" class="dialog-input" showIcon fluid
                  iconDisplay="input"/>
    </div>

    <div class="dialog-input-group" v-if="editingUser.userType === 'Admin'">
      <label for="adminLevel" class="dialog-label">Admin Level</label>
      <InputText id="adminLevel" v-model="editingUser.adminLevel" class="dialog-input"/>
    </div>

    <div class="dialog-button-group">
      <Button label="Cancel" severity="secondary" @click="editDialogVisible = false"/>
      <Button label="Save" @click="handleUpdateUser" :disabled="!isEditFormValid"/>
    </div>
  </Dialog>
</template>

<script setup>
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import DatePicker from 'primevue/datepicker';
import Button from 'primevue/button';
import RadioButton from 'primevue/radiobutton';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Dialog from "primevue/dialog";
import AutoComplete from "primevue/autocomplete";

import {computed, ref} from "vue";
import {createUser, getUsers, updateUser, deleteUser} from '@/service/ManageUsersService';

const name = ref('');
const surname = ref('');
const username = ref('');
const password = ref('');
const address = ref('');
const adminLevel = ref('');
const dateOfBirth = ref('');
const userType = ref('');
const users = ref([]);
const showDialog = ref(false);
const dialogMessage = ref('');
const editDialogVisible = ref(false);
const editingUser = ref({});
const adminLevelOptions = ref([
  {label: 'Basic', value: 'BASIC'},
  {label: 'Middle', value: 'MIDDLE'},
  {label: 'Super', value: 'SUPER'}
]);

const warningMessages = ref({
  name: '',
  surname: '',
  username: '',
});

const editWarningMessages = ref({
  name: '',
  surname: '',
});

const resetFormFields = () => {
  userType.value = null;
  name.value = '';
  surname.value = '';
  username.value = '';
  password.value = '';
  adminLevel.value = '';
  dateOfBirth.value = '';
  address.value = null;
};

const validators = {
  onlyLetters: (value) => /^[A-Za-z]*$/.test(value) ? '' : 'Only letters are allowed!',
  usernameValidator: (value) => /^[A-Za-z0-9!@#$%^&*()_+=-]*$/.test(value) ? '' : 'Username can contain letters, numbers, and special characters!'
};

const validateField = (field) => {
  switch (field) {
    case 'name':
      warningMessages.value.name = validators.onlyLetters(name.value);
      break;
    case 'surname':
      warningMessages.value.surname = validators.onlyLetters(surname.value);
      break;
    case 'username':
      warningMessages.value.username = validators.usernameValidator(username.value);
      break;
    default:
      break;
  }
};

const validateEditField = (field) => {
  switch (field) {
    case 'name':
      editWarningMessages.value.name = validators.onlyLetters(editingUser.value.name);
      break;
    case 'surname':
      editWarningMessages.value.surname = validators.onlyLetters(editingUser.value.surname);
      break;
    default:
      break;
  }
};

const isFormValid = computed(() => {
  if (!userType.value) return false;

  const commonFields = [name.value, surname.value, username.value, password.value];
  const noWarnings = Object.values(warningMessages.value).every(message => !message);

  if (userType.value === 'Client') {
    const clientSpecificFields = [dateOfBirth.value, address.value];
    return !commonFields.some(field => !field) && !clientSpecificFields.some(field => !field) && noWarnings;
  } else {
    return !commonFields.some(field => !field) && adminLevel.value && noWarnings;
  }
});

const isEditFormValid = computed(() => {
  const noWarnings = Object.values(editWarningMessages.value).every(message => !message);
  const requiredFields = ['name', 'surname', 'username'];
  const allFieldsFilled = requiredFields.every(field => editingUser.value[field]);

  if (editingUser.value.userType === 'Client') {
    return allFieldsFilled && editingUser.value.address && editingUser.value.dateOfBirth && noWarnings;
  } else {
    return allFieldsFilled && editingUser.value.adminLevel && noWarnings;
  }
});

const handleCreateUser = async () => {
  const userDto = {
    name: name.value,
    surname: surname.value,
    username: username.value,
    password: password.value,
    address: userType.value === 'Client' ? address.value : undefined,
    dateOfBirth: userType.value === 'Client' ? dateOfBirth.value : undefined,
    adminLevel: userType.value === 'Admin' ? adminLevel.value : undefined,
    userType: userType.value,
  };

  try {
    await createUser(userDto);
    dialogMessage.value = "User created successfully.";
    showDialog.value = true;
  } catch (error) {
    dialogMessage.value = error.message;
    showDialog.value = true;
  } finally {
    await resetFormFields();
    await handleGetUsers();
  }
};

const handleGetUsers = async () => {
  try {
    const data = await getUsers();
    console.log(data);
    users.value = data;
  } catch (error) {
    dialogMessage.value = error.message;
    showDialog.value = true;
  }
}


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
}

const openEditDialog = (user) => {
  editingUser.value = {...user};
  editDialogVisible.value = true;
};

const handleUpdateUser = async () => {
  try {
    await updateUser(editingUser.value.username, editingUser.value);
    dialogMessage.value = "User updated successfully.";
    showDialog.value = true;
    editDialogVisible.value = false;
  } catch (error) {
    dialogMessage.value = error.message;
    showDialog.value = true;
  } finally {
    await handleGetUsers();
  }
};

const searchAdminLevel = (event) => {
  const query = event.query.toLowerCase();
  adminLevelOptions.value = ['Basic', 'Middle', 'Super'].filter(option =>
      option.toLowerCase().includes(query)
  );
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

.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 40em;
  border: white 1px solid;
  padding: 3em 3em;
  margin: 2em 3em;
  background-color: rgb(24, 24, 27);
}

.card-user-list {
  margin: 1em 1em;
  padding-bottom: 2em;
}

.card-header {
  margin: 1em 1em;
}

.warning-message {
  color: red;
  font-weight: bold;
}

.edit-user-dialog {
  width: 35rem;
}

.dialog-input-group {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.dialog-label {
  font-weight: bold;
  width: 8rem;
}

.dialog-input {
  flex: 1;
}

.dialog-button-group {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}
</style>