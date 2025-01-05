<template>
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
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import DatePicker from 'primevue/datepicker';
import Button from 'primevue/button';
import RadioButton from 'primevue/radiobutton';
import AutoComplete from 'primevue/autocomplete';

const props = defineProps(['createUser']);
const emit = defineEmits(['userCreated']);

const userType = ref('');
const name = ref('');
const surname = ref('');
const username = ref('');
const password = ref('');
const address = ref('');
const adminLevel = ref('');
const dateOfBirth = ref('');
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

const handleCreateUser = async () => {
  const userDto = {
    name: name.value,
    surname: surname.value,
    username: username.value,
    password: password.value,
    address: userType.value === 'Client' ? address.value : undefined,
    dateOfBirth: userType.value === 'Client' ? dateOfBirth.value : undefined,
    adminLevel: userType.value === 'Admin' && adminLevel.value
        ? adminLevel.value.toUpperCase()
        : undefined,
    userType: userType.value,
  };

  try {
    await props.createUser(userDto);
    emit('userCreated');
    resetFormFields();
  } catch (error) {
    console.error(error);
  }
};

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

const searchAdminLevel = (event) => {
  const query = event.query.toLowerCase();
  adminLevelOptions.value = ['Basic', 'Middle', 'Super'].filter(option =>
      option.toLowerCase().includes(query)
  );
};
</script>

<style scoped>
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

.warning-message {
  color: red;
  font-weight: bold;
}
</style>