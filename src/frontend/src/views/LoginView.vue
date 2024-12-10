<template>
  <div class="card">
    <DialogMessage
        v-model:visible="showDialog"
        title="Warning"
        message="Login failed. Please try again."
    />

    <div class="flex-container">
      <div class="login-section">
        <div class="input-group">
          <label for="username">Username</label>
          <InputText id="username" v-model="username" type="text" />
        </div>
        <div class="input-group">
          <label for="password">Password</label>
          <InputText id="password" v-model="password" type="password" />
        </div>
        <div class="button-container">
          <Button label="Login" icon="pi pi-user" class="full-width-button" @click="handleLogin" />
        </div>
      </div>
      <div class="divider-section">
        <Divider layout="horizontal" class="horizontal-divider" align="center"><b>OR</b></Divider>
      </div>
      <div class="signup-section">
        <Button label="Sign Up" icon="pi pi-user-plus" severity="success" class="full-width-button" />
      </div>
    </div>
  </div>
</template>

<script setup>
import Divider from 'primevue/divider';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import { useRouter } from 'vue-router';
import { ref } from "vue";
import { login, getCurrentUserRoles } from "@/service/AuthenticationService";
import DialogMessage from '@/components/DialogMessage.vue';

const router = useRouter();
const username = ref('');
const password = ref('');
const showDialog = ref(false);

const handleLogin = async () => {
  const loginRequestDto = {
    username: username.value,
    password: password.value
  };

  try {
    const response = await login(loginRequestDto);
    if (response === username.value) {
      const roles = await getCurrentUserRoles();
      if (roles.includes('ROLE_CLIENT')) {
        await router.push('/publications');
      } else if (roles.includes('ROLE_ADMIN')) {
        await router.push('/users');
      } else {
        showDialog.value = true;
      }
    }
  } catch (error) {
    console.error("Login failed", error);
    showDialog.value = true;
  }
};


</script>

<style scoped>
.card {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1rem;
}

.flex-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 30rem;
  padding: 2rem;
}

.login-section, .signup-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 100%;
}

.button-container {
  width: 100%;
  margin-top: 0.5rem;
}

.full-width-button {
  width: 100%;
}

.divider-section {
  width: 100%;
  margin: 1.5rem 0;
}

.horizontal-divider {
  display: flex;
}

label {
  font-weight: bold;
}

@media (max-width: 480px) {
  .flex-container {
    padding: 1rem;
  }
}
</style>