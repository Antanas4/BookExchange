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
      <InputText id="name" v-model="name" placeholder="Enter Name"/>
      <InputText id="surname" v-model="surname" placeholder="Enter Surname"/>
      <InputText id="username" v-model="username" placeholder="Enter Username"/>
      <Password id="password" v-model="password" :feedback="false" placeholder="Enter Password"/>
      <DatePicker v-if="userType === 'Client'" v-model="dateOfBirth" showIcon fluid iconDisplay="input" placeholder="Enter Date Of Birth"
                  :disabled="userType === 'Admin'"/>
      <InputText id="address" v-if="userType === 'Client'" v-model="address" placeholder="Enter Address" :disabled="userType === 'Admin'"/>
      <InputText id="admin-level" v-if="userType === 'Admin'" v-model="adminLevel" placeholder="Admin Level"
                 :disabled="userType === 'Client'"/>
      <p v-if="warningMessage" class="warning-message">{{ warningMessage }}</p>
      <Button label="Create" severity="success" @click="createUser"/>
      <Button label="Update" severity="warn" @click="updateUser"/>
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
</template>
<script setup>

import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import DatePicker from 'primevue/datepicker';
import Button from 'primevue/button';
import RadioButton from 'primevue/radiobutton';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import axios from 'axios';

import {ref} from "vue";

const name = ref('');
const surname = ref('');
const username = ref('');
const password = ref('');
const address = ref('');
const adminLevel = ref('');
const dateOfBirth = ref('');
const userType = ref('');
const users = ref([]);
const warningMessage = ref('');

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

const validateUserInput = () => {
  warningMessage.value = '';

  if (!userType.value) {
    warningMessage.value = 'Please choose user type.';
    return false;
  }
  const commonFields = [name.value, surname.value, username.value, password.value];
  if (userType.value === 'Client') {
    const clientSpecificFields = [dateOfBirth.value, address.value];
    if (commonFields.some(field => !field) || clientSpecificFields.some(field => !field)) {
      warningMessage.value = 'Please fill all the fields.';
      return false;
    }
  } else {
    if (commonFields.some(field => !field) || !adminLevel.value) {
      warningMessage.value = 'Please fill all the fields.';
      return false;
    }
  }
  return true;
};

const createUser = async () => {
  if (!validateUserInput()) {
    return;
  }

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
    console.log("User exists: ", error);
    warningMessage.value = 'User with this username already exists';
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
    console.log('Fetched users:', response.data);
    users.value = response.data;
  } catch (error) {
    console.error('Error fetching users:', error);
  }
}

const updateUser = async () => {
  if (!validateUserInput()) {
    return;
  }

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
      console.log('User found:', userCheckResponse.data);

      const response = await axios.put(`/api/users/${username.value}`, userDto);
      console.log('User updated:', response.data);

    } else {
      console.warn('User with that username does not exist.');
    }
  } catch (error) {
    if (error.response) {
      console.error('Error updating user:', error.response.data);
    } else {
      console.error('Error updating user:', error.message);
    }
  } finally {
    await fetchUserData();
  }
}

const deleteUser = async (usernameToDelete) => {
  try {
    const response = await axios.delete(`/api/users/${usernameToDelete}`)
    console.log('User deleted:', response.data);
    await fetchUserData();
  } catch (error) {
    console.log('Deleting user with username:', usernameToDelete);
    console.error('Error deleting user:', error);
  }
}

// const validateOnlyLetters = (value) => {
//   const isValid = /^[A-Za-z]*$/.test(value);
//   return isValid ? '' : 'Only letters are allowed!';
// };
//
// const validateOnlyNumbers = (value) => {
//   const isValid = /^\d*\.?\d*$/.test(value); // Allows decimals
//   return isValid ? '' : 'Only numbers and a dot are allowed!';
// };

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
