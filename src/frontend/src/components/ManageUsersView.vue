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
      <DatePicker v-if="userType === 'Client'" v-model="dateOfBirth" showIcon fluid iconDisplay="input" placeholder="Enter Date Of Birth"
                  :disabled="userType === 'Admin'"/>
      <InputText id="address" v-if="userType === 'Client'" v-model="address" placeholder="Enter Address" :disabled="userType === 'Admin'"/>
      <InputText id="admin-level" v-if="userType === 'Admin'" v-model="adminLevel" placeholder="Admin Level"
                 :disabled="userType === 'Client'"/>
      <Button label="Create" severity="success" @click="createUser" :disabled="!isFormValid"/>
      <Button label="Update" severity="warn" @click="updateUser" :disabled="!isFormValid"/>
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
            <Button icon="pi pi-trash" outlined rounded severity="danger" @click="deleteUser(slotProps.data.username)"/>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>

  <Dialog v-model:visible="showDialog" header="Warning" :modal="true" :closable="false">
    <p>{{ dialogMessage }}</p>
    <Button label="OK" @click="showDialog = false" />
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
import axios from 'axios';

import {computed, ref} from "vue";

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

let userDto = ref({
  name: '',
  surname: '',
  username: '',
  password: '',
  address: '',
  adminLevel: '',
  dateOfBirth: '',
  userType: ''
});

const warningMessages = ref({
  name:'',
  surname:'',
});

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

const createUser = async () => {

  userDto = {
    name: name.value,
    surname: surname.value,
    username: username.value,
    password: password.value,
    address: userType.value === 'Client' ? address.value : undefined,
    dateOfBirth: userType.value === 'Client' ? dateOfBirth.value : undefined,
    adminLevel: userType.value === 'Admin' ? adminLevel.value : undefined,
    userType: userType.value
  };

  try {
    const response = await axios.post('/api/users', userDto);
    console.log("User created", response.data);
  } catch (error){
    dialogMessage.value('Username is already taken');
    showDialog.value = true;
  } finally {
    name.value = '';
    surname.value = '';
    username.value = '';
    password.value = '';
    address.value = '';
    dateOfBirth.value = '';
    adminLevel.value = '';
    userType.value = '';
    await fetchUserData();
  }

};

const fetchUserData = async () => {
  try {
    const response = await axios.get('/api/users');
    users.value = response.data;
  } catch (error) {
    dialogMessage.value = "Error fetching user data."
    showDialog.value = true;
  }
}

const updateUser = async () => {

  userDto = {
    name: name.value,
    surname: surname.value,
    username: username.value,
    password: password.value,
    address: userType.value === 'Client' ? address.value : undefined,
    dateOfBirth: userType.value === 'Client' ? dateOfBirth.value : undefined,
    adminLevel: userType.value === 'Admin' ? adminLevel.value : undefined,
    userType: userType.value
  };

  try {
    const userCheckResponse = await axios.get(`/api/users/${username.value}`);
    if (userCheckResponse.data) {
      await axios.put(`/api/users/${username.value}`, userDto);
      dialogMessage.value = "User created successfully.";
      showDialog.value = true;
    }
  } catch (error) {
    dialogMessage.value = "User " + `${username.value}` + " does not exist.";
    showDialog.value = true;
  } finally {
    await fetchUserData();
  }
}

const deleteUser = async (usernameToDelete) => {
  try {
    await axios.delete(`/api/users/${usernameToDelete}`)
    dialogMessage.value = "User deleted successfully."
    showDialog.value = true;
  } catch (error) {
    dialogMessage.value = "An error occurred while deleting the user."
    showDialog.value = true;
  } finally {
    await fetchUserData();
  }
}

fetchUserData();

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

</style>
