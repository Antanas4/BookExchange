<template>
  <h1 class="page-header">Manage Publications</h1>
  <div class="container">
    <div class="form">
      <InputText v-model="ownerUsername" placeholder="Enter Publications Owner Username"
                 @input="validateField('ownerUsername')"/>
      <p v-if="warningMessages.ownerUsername" class="warning-message">{{ warningMessages.ownerUsername }}</p>

      <InputText v-model="title" placeholder="Enter Title" @input="validateField('title')"/>
      <p v-if="warningMessages.title" class="warning-message">{{ warningMessages.title }}</p>

      <InputText v-model="author" placeholder="Enter Author Name" @input="validateField('author')"/>
      <p v-if="warningMessages.author" class="warning-message">{{ warningMessages.author }}</p>

      <InputText v-model="price" placeholder="Enter Price" @input="validateField('price')"/>
      <p v-if="warningMessages.price" class="warning-message">{{ warningMessages.price }}</p>

      <AutoComplete v-model="selectedType" :suggestions="filteredTypes" @complete="searchTypes"
                    :virtualScrollerOptions="{ itemSize: 38 }" optionLabel="label" dropdown forceSelection
                    placeholder="Select Publication Type"/>

      <InputText v-if="selectedType?.label === 'Magazine'" v-model="issueNumber"
                 placeholder="Enter Magazine's Issue Number" @input="validateField('issueNumber')"/>
      <p v-if="warningMessages.issueNumber" class="warning-message">{{ warningMessages.issueNumber }}</p>

      <InputText v-if="selectedType?.label === 'Comic Book'" v-model="illustrator"
                 placeholder="Enter Comic Book's illustrator" @input="validateField('illustrator')"/>
      <p v-if="warningMessages.illustrator" class="warning-message">{{ warningMessages.illustrator }}</p>

      <InputText v-if="selectedType?.label === 'Book'" v-model="genre" placeholder="Enter Book's genre"
                 @input="validateField('genre')"/>
      <p v-if="warningMessages.genre" class="warning-message">{{ warningMessages.genre }}</p>

      <p v-if="warningMessage" class="warning-message">{{ warningMessage }}</p>
      <Button label="Add Book" severity="success" @click="addPublication" :disabled="!isFormValid"/>
    </div>

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
                    @click="deletePublication(slotProps.data.id)"/>
          </template>
        </Column>
      </DataTable>
    </div>
    <Dialog v-model:visible="visibleDialog" modal header="Edit Publication" class="edit-publication-dialog">
      <div class="dialog-input-group">
        <label for="author" class="dialog-label">Author</label>
        <InputText id="author" v-model="publicationToEdit.author" class="dialog-input" autocomplete="off"/>
      </div>

      <div class="dialog-input-group">
        <label for="title" class="dialog-label">Title</label>
        <InputText id="title" v-model="publicationToEdit.title" class="dialog-input" autocomplete="off"/>
      </div>

      <div class="dialog-input-group">
        <label for="price" class="dialog-label">Price</label>
        <InputText id="price" v-model="publicationToEdit.price" class="dialog-input" autocomplete="off"/>
      </div>

      <div class="dialog-input-group" v-if="publicationToEdit.publicationType === 'Magazine'">
        <label for="issueNumber" class="dialog-label">Issue Number</label>
        <InputText id="issueNumber" v-model="publicationToEdit.issueNumber" class="dialog-input" autocomplete="off"/>
      </div>

      <div class="dialog-input-group" v-if="publicationToEdit.publicationType === 'Comic Book'">
        <label for="illustrator" class="dialog-label">Illustrator</label>
        <InputText id="illustrator" v-model="publicationToEdit.illustrator" class="dialog-input" autocomplete="off"/>
      </div>

      <div class="dialog-input-group" v-if="publicationToEdit.publicationType === 'Book'">
        <label for="genre" class="dialog-label">Genre</label>
        <InputText id="genre" v-model="publicationToEdit.genre" class="dialog-input" autocomplete="off"/>
      </div>

      <div class="dialog-button-group">
        <Button type="button" label="Cancel" severity="secondary" @click="visibleDialog = false"></Button>
        <Button type="button" label="Save" @click="savePublication"></Button>
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
import AutoComplete from "primevue/autocomplete";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Dialog from "primevue/dialog";
import {computed, ref} from "vue";
import {
  getPublicationsService,
  updatePublicationService,
  deletePublicationService,
  createPublicationService
} from "@/service/ManagePublicationsService";

const ownerUsername = ref('');
const title = ref('');
const author = ref('');
const price = ref('');
const genre = ref('');
const issueNumber = ref('');
const illustrator = ref('');
const selectedType = ref(null);
const warningMessage = ref('');
const publications = ref([]);
const publicationToEdit = ref(null);
const visibleDialog = ref(false);
const showDialog = ref(false);
const dialogMessage = ref('');

const warningMessages = ref({
  ownerUsername: '',
  title: '',
  author: '',
  price: '',
  genre: '',
  issueNumber: '',
  illustrator: '',
});


const publicationTypes = [
  {label: 'Book'},
  {label: 'Magazine'},
  {label: 'Comic Book'}
];

let publicationDto = ref({
  id: '',
  ownerUsername: '',
  title: '',
  author: '',
  price: '',
  genre: '',
  issueNumber: '',
  illustrator: '',
  publicationType: ''
});


const filteredTypes = ref([]);

const searchTypes = (event) => {
  filteredTypes.value = publicationTypes.filter(type =>
      type.label.toLowerCase().includes(event.query.toLowerCase()));
}

const validators = {
  onlyLetters: (value) => /^[A-Za-z]*$/.test(value) ? '' : 'Only letters are allowed!',
  onlyNumbers: (value) => /^\d*\.?\d*$/.test(value) ? '' : 'Only numbers and a dot are allowed!',
};

const validateField = (field) => {
  switch (field) {
    case 'ownerUsername':
      warningMessages.value.ownerUsername = validators.onlyLetters(ownerUsername.value);
      break;
    case 'title':
      break;
    case 'author':
      warningMessages.value.author = validators.onlyLetters(author.value);
      break;
    case 'price':
      warningMessages.value.price = validators.onlyNumbers(price.value);
      break;
    case 'issueNumber':
      warningMessages.value.issueNumber = selectedType.value?.label === 'Magazine' ? validators.onlyNumbers(issueNumber.value) : '';
      break;
    case 'illustrator':
      warningMessages.value.illustrator = selectedType.value?.label === 'Comic Book' ? validators.onlyLetters(illustrator.value) : '';
      break;
    case 'genre':
      warningMessages.value.genre = selectedType.value?.label === 'Book' ? validators.onlyLetters(genre.value) : '';
      break;
    default:
      break;
  }
};

const validateFormInput = () => {
  Object.values(warningMessages.value).forEach(message => {
    if (message) {
      warningMessage.value = 'Please fix the highlighted errors before submitting.';
      return false;
    }
  });
  warningMessage.value = '';
  return true;
};

const isFormValid = computed(() => {
  const noWarnings = Object.values(warningMessages.value).every(message => !message);
  const allFieldsFilled = ownerUsername.value && title.value && author.value && price.value && selectedType.value &&
      (selectedType.value.label !== 'Book' || genre.value) &&
      (selectedType.value.label !== 'Magazine' || issueNumber.value) &&
      (selectedType.value.label !== 'Comic Book' || illustrator.value);
  return noWarnings && allFieldsFilled;
});

const resetFormFields = () => {
  ownerUsername.value = '';
  title.value = '';
  author.value = '';
  price.value = '';
  genre.value = '';
  issueNumber.value = '';
  illustrator.value = '';
  selectedType.value = null;
};

const addPublication = async () => {
  if (!validateFormInput()) return;

  publicationDto = {
    ownerUsername: ownerUsername.value,
    title: title.value,
    author: author.value,
    price: price.value,
    genre: selectedType.value?.label === 'Book' ? genre.value : undefined,
    issueNumber: selectedType.value?.label === 'Magazine' ? issueNumber.value : undefined,
    illustrator: selectedType.value?.label === 'Comic Book' ? illustrator.value : undefined,
    publicationType: selectedType.value?.label
  };

  try {
    await createPublicationService(publicationDto, ownerUsername.value);
    await loadPublications();
  } catch (error) {
    dialogMessage.value = 'Client ' + `${ownerUsername.value}` + ' does not exist.';
    showDialog.value = true;
  } finally {
    resetFormFields();
  }
};


const deletePublication = async (publicationId) => {
  try {
    await deletePublicationService(publicationId);
    await loadPublications();
  } catch (error) {
    dialogMessage.value = 'Server error deleting publications';
    showDialog.value = true;
  }
};

const loadPublications = async () => {
  try {
    await getPublicationsService(publications);
  } catch (error) {
    dialogMessage.value = 'Server error loading publications.';
    showDialog.value = true;
  }
}

const openEditDialog = async (publicationToEditData) => {
  publicationToEdit.value = publicationToEditData;
  visibleDialog.value = true;
}

const savePublication = async () => {
  try {
    if (publicationToEdit.value) {
      await updatePublicationService(publicationToEdit.value);
      visibleDialog.value = false;
    }
  } catch (error) {
    dialogMessage.value = 'Server error editing publication.';
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

.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 40em;
  border: white 1px solid;
  background-color: rgb(24, 24, 27);
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
  width: 25rem;
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

