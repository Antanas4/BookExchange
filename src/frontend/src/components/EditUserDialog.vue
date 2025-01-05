<template>
  <Dialog v-model:visible="localVisible" modal header="Edit User" class="edit-user-dialog">
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
      <Button label="Cancel" severity="secondary" @click="closeDialog"/>
      <Button label="Save" @click="handleUpdateUser" :disabled="!isEditFormValid"/>
    </div>
  </Dialog>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits, watch } from 'vue';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import DatePicker from 'primevue/datepicker';
import Button from 'primevue/button';

const props = defineProps(['user', 'visible', 'updateUser']);
const emit = defineEmits(['update:visible', 'userUpdated']);

const localVisible = ref(props.visible);
const editingUser = ref({ ...props.user });
const editWarningMessages = ref({
  name: '',
  surname: '',
});

const validators = {
  onlyLetters: (value) => /^[A-Za-z]*$/.test(value) ? '' : 'Only letters are allowed!',
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

const handleUpdateUser = async () => {
  try {
    await props.updateUser(editingUser.value.username, editingUser.value);
    emit('userUpdated');
    closeDialog();
  } catch (error) {
    console.error(error);
  }
};

const closeDialog = () => {
  emit('update:visible', false);
};

watch(() => props.user, (newUser) => {
  editingUser.value = { ...newUser };
}, { immediate: true });

watch(() => props.visible, (newVal) => {
  localVisible.value = newVal;
  if (newVal) {
    editingUser.value = { ...props.user };
  }
});
</script>

<style scoped>
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

.warning-message {
  color: red;
  font-weight: bold;
}
</style>