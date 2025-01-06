<template>
  <div class="form">
    <label for="title" class="dialog-label">Title</label>
    <InputText v-model="title" placeholder="Enter Title" @input="validateField('title')" />
    <p v-if="formInputWarning.title" class="warning-message">{{ formInputWarning.title }}</p>

    <label for="author" class="dialog-label">Author</label>
    <InputText v-model="author" placeholder="Enter Author Name" @input="validateField('author')" />
    <p v-if="formInputWarning.author" class="warning-message">{{ formInputWarning.author }}</p>

    <label for="price" class="dialog-label">Price</label>
    <InputText v-model="price" placeholder="Enter Price" @input="validateField('price')" />
    <p v-if="formInputWarning.price" class="warning-message">{{ formInputWarning.price }}</p>

    <label for="Publication type" class="dialog-label">Publication Type</label>
    <AutoComplete
        v-model="selectedType"
        :suggestions="filteredTypes"
        @complete="searchTypes"
        :virtualScrollerOptions="{ itemSize: 38 }"
        optionLabel="label"
        dropdown
        forceSelection
        placeholder="Select Publication Type"
    />

    <label v-if="selectedType?.label === 'Magazine'" for="issueNumber" class="dialog-label">Issue Number</label>
    <InputText
        v-if="selectedType?.label === 'Magazine'"
        v-model="issueNumber"
        placeholder="Enter Magazine's Issue Number"
        @input="validateField('issueNumber')"
    />
    <p v-if="formInputWarning.issueNumber" class="warning-message">{{ formInputWarning.issueNumber }}</p>

    <label v-if="selectedType?.label === 'Comic Book'" for="illustrator" class="dialog-label">Illustrator</label>
    <InputText
        v-if="selectedType?.label === 'Comic Book'"
        v-model="illustrator"
        placeholder="Enter Comic Book's illustrator"
        @input="validateField('illustrator')"
    />
    <p v-if="formInputWarning.illustrator" class="warning-message">{{ formInputWarning.illustrator }}</p>

    <label v-if="selectedType?.label === 'Book'" for="genre" class="dialog-label">Genre</label>
    <InputText
        v-if="selectedType?.label === 'Book'"
        v-model="genre"
        placeholder="Enter Book's Genre"
        @input="validateField('genre')"
    />
    <p v-if="formInputWarning.genre" class="warning-message">{{ formInputWarning.genre }}</p>

    <p v-if="warningMessage" class="warning-message">{{ warningMessage }}</p>
    <Button label="Add Publication" severity="success"
            @click="handleCreatePublication" :disabled="!isFormValid" />
  </div>
</template>

<script setup>
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import AutoComplete from "primevue/autocomplete";
import {computed, defineEmits, ref} from "vue";
import {createPublication} from "@/service/ManagePublicationsService";
import {useRoute} from "vue-router";

const ownerUsername = ref('');
const title = ref('');
const author = ref('');
const price = ref('');
const genre = ref('');
const issueNumber = ref('');
const illustrator = ref('');
const selectedType = ref(null);
const warningMessage = ref('');
const route = useRoute();
const clientUsername = route.params.clientUsername;
const emit = defineEmits(['update-my-publications']);
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
  usernameValidator: (value) => /^[A-Za-z0-9!@#$%^&*()_+=-]*$/.test(value) ? '' : 'Username can contain letters, numbers, and special characters!',
  onlyLettersAndSpaces: (value) => /^[A-Za-z\s]*$/.test(value) ? '' : 'Only letters and spaces are allowed!',
  onlyLetters: (value) => /^[A-Za-z]*$/.test(value) ? '' : 'Only letters are allowed!',
  onlyNumbersAndDot: (value) => /^\d*\.?\d*$/.test(value) ? '' : 'Only numbers and a dot are allowed!',
};

const validateField = (field) => {
  switch (field) {
    case 'ownerUsername':
      formInputWarning.value.ownerUsername = validators.usernameValidator(ownerUsername.value);
      break;
    case 'title':
      break;
    case 'author':
      formInputWarning.value.author = validators.onlyLettersAndSpaces(author.value);
      break;
    case 'price':
      formInputWarning.value.price = validators.onlyNumbersAndDot(price.value);
      break;
    case 'issueNumber':
        formInputWarning.value.issueNumber = selectedType.value?.label === 'Magazine' ? validators.onlyNumbersAndDot(issueNumber.value) : '';
      break;
    case 'illustrator':
        formInputWarning.value.illustrator = selectedType.value?.label === 'Comic Book' ? validators.onlyLetters(illustrator.value) : '';
      break;
    case 'genre':
        formInputWarning.value.genre = selectedType.value?.label === 'Book' ? validators.onlyLetters(genre.value) : '';
      break;
    default:
      break;
  }
};


const validateFormInput = () => {
  Object.values(formInputWarning.value).forEach(message => {
    if (message) {
      warningMessage.value = 'Please fix the highlighted errors before submitting.';
      return false;
    }
  });
  warningMessage.value = '';
  return true;
};

const isFormValid = computed(() => {
  const noWarnings = Object.values(formInputWarning.value).every(message => !message);
  const allFieldsFilled = title.value && author.value && price.value && selectedType.value &&
      (selectedType.value.label !== 'Book' || genre.value) &&
      (selectedType.value.label !== 'Magazine' || issueNumber.value) &&
      (selectedType.value.label !== 'Comic Book' || illustrator.value);
  return noWarnings && allFieldsFilled;
});

const handleCreatePublication = async () => {
  if (!validateFormInput()) return;

  publicationDto = {
    ownerUsername: clientUsername,
    title: title.value,
    author: author.value,
    price: price.value,
    genre: selectedType.value?.label === 'Book' ? genre.value : undefined,
    issueNumber: selectedType.value?.label === 'Magazine' ? issueNumber.value : undefined,
    illustrator: selectedType.value?.label === 'Comic Book' ? illustrator.value : undefined,
    publicationType: selectedType.value?.label
  };

  try {
    await createPublication(publicationDto, clientUsername);
    emit('update-my-publications');
  } catch (error) {
    dialogMessage.value = 'Client ' + `${clientUsername}` + ' does not exist.';
    showDialog.value = true;
  } finally {
    resetFormFields();
  }
};

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

</script>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 40em;
}
</style>