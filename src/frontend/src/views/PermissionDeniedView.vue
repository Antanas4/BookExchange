<template>
  <div class="permission-denied">
    <h1>403 - Permission Denied</h1>
    <p>You do not have permission to access this page.</p>
  </div>
</template>

<script>
export default {
  name: "PermissionDeniedView",
};
</script>

<style scoped>
.permission-denied {
  text-align: center;
  margin-top: 100px;
}
.permission-denied h1 {
  font-size: 2.5em;
  color: #ff4d4f;
}
.permission-denied p {
  margin: 20px 0;
  font-size: 1.2em;
}
</style>
