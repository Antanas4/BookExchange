<template>
  <AdminMenuBar/>
  <h1 class="page-header">Manage Publications</h1>
  <div class="container">
    <div class="card-publication-list">
      <h2 class="card-header">Publications List</h2>
      <DataTable :value="publications" tableStyle="min-width: 50rem">
        <Column field="id" header="ID"/>
        <Column field="author" header="Author"/>
        <Column field="title" header="Title"/>
        <Column field="price" header="Price"/>
        <Column field="publicationType" header="Publication Type"/>
        <Column field="ownerUsername" header="Owner Username"/>
        <Column header="Actions">
          <template #body="slotProps">
            <Button icon="pi pi-pencil" outlined rounded @click="openEditDialog(slotProps.data)"/>
            <Button icon="pi pi-trash" outlined rounded severity="danger"
                    @click="handleDeletePublication(slotProps.data.id)"/>
          </template>
        </Column>
      </DataTable>
    </div>
    <Dialog v-model:visible="visibleDialog" modal header="Edit Publication" class="edit-publication-dialog">
      <div class="dialog-input-group">
        <label for="author" class="dialog-label">Author</label>
        <InputText id="author" v-model="publicationToEdit.author" class="dialog-input" autocomplete="off"
                   @input="validateField('author', 'dialog')"/>
        <p v-if="dialogInputWarning.author" class="warning-message">{{ dialogInputWarning.author }}</p>
      </div>

      <div class="dialog-input-group">
        <label for="title" class="dialog-label">Title</label>
        <InputText id="title" v-model="publicationToEdit.title" class="dialog-input" autocomplete="off"
                   @input="validateField('title', 'dialog')"/>
        <p v-if="dialogInputWarning.title" class="warning-message">{{ dialogInputWarning.title }}</p>
      </div>

      <div class="dialog-input-group">
        <label for="price" class="dialog-label">Price</label>
        <InputText id="price" v-model="publicationToEdit.price" class="dialog-input" autocomplete="off"
                   @input="validateField('price', 'dialog')"/>
        <p v-if="dialogInputWarning.price" class="warning-message">{{ dialogInputWarning.price }}</p>
      </div>

      <div class="dialog-input-group" v-if="publicationToEdit.publicationType === 'Magazine'">
        <label for="issueNumber" class="dialog-label">Issue Number</label>
        <InputText id="issueNumber" v-model="publicationToEdit.issueNumber" class="dialog-input" autocomplete="off"
                   @input="validateField('issueNumber', 'dialog')"/>
        <p v-if="dialogInputWarning.issueNumber" class="warning-message">{{ dialogInputWarning.issueNumber }}</p>
      </div>

      <div class="dialog-input-group" v-if="publicationToEdit.publicationType === 'Comic Book'">
        <label for="illustrator" class="dialog-label">Illustrator</label>
        <InputText id="illustrator" v-model="publicationToEdit.illustrator" class="dialog-input" autocomplete="off"
                   @input="validateField('illustrator', 'dialog')"/>
        <p v-if="dialogInputWarning.illustrator" class="warning-message">{{ dialogInputWarning.illustrator }}</p>
      </div>

      <div class="dialog-input-group" v-if="publicationToEdit.publicationType === 'Book'">
        <label for="genre" class="dialog-label">Genre</label>
        <InputText id="genre" v-model="publicationToEdit.genre" class="dialog-input" autocomplete="off"
                   @input="validateField('genre', 'dialog')"/>
        <p v-if="dialogInputWarning.genre" class="warning-message">{{ dialogInputWarning.genre }}</p>
      </div>

      <div class="dialog-button-group">
        <Button type="button" label="Cancel" severity="secondary" @click="visibleDialog = false"></Button>
        <Button type="button" label="Save" @click="savePublication" :disabled="!isDialogValid"></Button>
      </div>
    </Dialog>
    <Dialog v-model:visible="showDialog" header="Warning" :modal="true" :closable="false">
      <p>{{ dialogMessage }}</p>
      <Button label="OK" @click="showDialog = false"/>
    </Dialog>
  </div>

</template>
<script setup>

import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Dialog from "primevue/dialog";
import {computed, ref} from "vue";
import {
  getPublications,
  updatePublication,
  deletePublication,
} from "@/service/ManagePublicationsService";
import AdminMenuBar from "@/components/AdminMenuBar.vue";

const ownerUsername = ref('');
const author = ref('');
const price = ref('');
const genre = ref('');
const issueNumber = ref('');
const illustrator = ref('');
const selectedType = ref(null);
const publications = ref([]);
const publicationToEdit = ref(null);
const visibleDialog = ref(false);
const showDialog = ref(false);
const dialogMessage = ref('');

const formInputWarning = ref({
  ownerUsername: '',
  title: '',
  author: '',
  price: '',
  genre: '',
  issueNumber: '',
  illustrator: '',
});
const dialogInputWarning = ref({
  title: '',
  author: '',
  price: '',
  genre: '',
  issueNumber: '',
  illustrator: '',
});

const validators = {
  usernameValidator: (value) => /^[A-Za-z0-9!@#$%^&*()_+=-]*$/.test(value) ? '' : 'Username can contain letters, numbers, and special characters!',
  onlyLettersAndSpaces: (value) => /^[A-Za-z\s]*$/.test(value) ? '' : 'Only letters and spaces are allowed!',
  onlyLetters: (value) => /^[A-Za-z]*$/.test(value) ? '' : 'Only letters are allowed!',
  onlyNumbersAndDot: (value) => /^\d*\.?\d*$/.test(value) ? '' : 'Only numbers and a dot are allowed!',
};

const validateField = (field, context) => {
  const warningObj = context === 'dialog' ? dialogInputWarning.value : formInputWarning.value;

  switch (field) {
    case 'ownerUsername':
      warningObj.ownerUsername = validators.usernameValidator(ownerUsername.value);
      break;
    case 'title':
      break;
    case 'author':
      warningObj.author = context === 'form' ? validators.onlyLettersAndSpaces(author.value) : validators.onlyLettersAndSpaces(publicationToEdit.value.author);
      break;
    case 'price':
      warningObj.price = context === 'form' ? validators.onlyNumbersAndDot(price.value) : validators.onlyNumbersAndDot(publicationToEdit.value.price);
      break;
    case 'issueNumber':
      if (context === 'dialog' && publicationToEdit.value.publicationType === 'Magazine') {
        warningObj.issueNumber = validators.onlyNumbersAndDot(publicationToEdit.value.issueNumber);
      } else {
        warningObj.issueNumber = selectedType.value?.label === 'Magazine' ? validators.onlyNumbersAndDot(issueNumber.value) : '';
      }
      break;
    case 'illustrator':
      if (context === 'dialog' && publicationToEdit.value.publicationType === 'Comic Book') {
        warningObj.illustrator = validators.onlyLettersAndSpaces(publicationToEdit.value.illustrator);
      } else {
        warningObj.illustrator = selectedType.value?.label === 'Comic Book' ? validators.onlyLetters(illustrator.value) : '';
      }
      break;
    case 'genre':
      if (context === 'dialog' && publicationToEdit.value.publicationType === 'Book') {
        warningObj.genre = validators.onlyLettersAndSpaces(publicationToEdit.value.genre);
      } else {
        warningObj.genre = selectedType.value?.label === 'Book' ? validators.onlyLetters(genre.value) : '';
      }
      break;
    default:
      break;
  }
};

const isDialogValid = computed(() => {
  const noWarnings = Object.values(dialogInputWarning.value).every(message => !message);
  const allFieldsFilled = publicationToEdit.value &&
      publicationToEdit.value.author &&
      publicationToEdit.value.title &&
      publicationToEdit.value.price &&
      (publicationToEdit.value.publicationType !== 'Book' || publicationToEdit.value.genre) &&
      (publicationToEdit.value.publicationType !== 'Magazine' || publicationToEdit.value.issueNumber) &&
      (publicationToEdit.value.publicationType !== 'Comic Book' || publicationToEdit.value.illustrator);

  return noWarnings && allFieldsFilled;
});

const handleDeletePublication = async (publicationId) => {
  try {
    await deletePublication(publicationId);
    await loadPublications();
  } catch (error) {
    dialogMessage.value = 'Failed to delete publication.';
    showDialog.value = true;
  }
};

const loadPublications = async () => {
  try {
    await getPublications(publications);
  } catch (error) {
    dialogMessage.value = 'Failed to fetch publications.';
    showDialog.value = true;
  }
}

const openEditDialog = async (publicationToEditData) => {
  publicationToEdit.value = JSON.parse(JSON.stringify(publicationToEditData));
  visibleDialog.value = true;
}

const savePublication = async () => {
  try {
    if (publicationToEdit.value) {
      await updatePublication(publicationToEdit.value);
      visibleDialog.value = false;
    }
  } catch (error) {
    dialogMessage.value = 'Failed to update publication';
    showDialog.value = true;
  } finally {
    await loadPublications();
  }
};

loadPublications();
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

.warning-message {
  color: red;
  font-weight: bold;
}

.edit-publication-dialog {
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

