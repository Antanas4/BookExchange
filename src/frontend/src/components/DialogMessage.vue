<template>
  <Dialog v-model:visible="localVisible" :header="title" :modal="true" :closable="false">
    <p>{{ message }}</p>
    <div class="dialog-actions">
      <Button label="Close" @click="closeDialog" class="p-button-secondary" />
    </div>
  </Dialog>
</template>

<script>
import { defineComponent, ref, watch } from 'vue';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';

export default defineComponent({
  components: {
    Dialog,
    Button,
  },
  props: {
    title: {
      type: String,
      required: true,
    },
    message: {
      type: String,
      required: true,
    },
    visible: {
      type: Boolean,
      required: true,
    },
  },
  setup(props, {emit}) {
    const localVisible = ref(props.visible);

    watch(() => props.visible, (newVal) => {
      localVisible.value = newVal;
    });

    const closeDialog = () => {
      localVisible.value = false;
      emit('update:visible', false);
    };

    return {
      localVisible,
      closeDialog,
    };
  },
});
</script>

<style scoped>
.dialog-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}
</style>
