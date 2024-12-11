<template>
  <ClientMenuBar v-if="CurrentUserRole === 'ROLE_CLIENT'"/>
  <AdminMenuBar v-if="CurrentUserRole === 'ROLE_ADMIN'"/>

  <div class="mainContainer">
    <div class="contentWrapper">
      <div class="clientDetailsBox" v-if="client">
        <h1>{{ client.name }}</h1>
        <p><strong>Name:</strong> {{ client.name }}</p>
        <p><strong>Surname:</strong> {{ client.surname }}</p>
        <p><strong>Username:</strong> {{ client.username }}</p>
      </div>

      <div class="reviewsBox">
        <h2>Reviews</h2>
        <VirtualScroller
            :items="reviews"
            :itemSize="120"
            class="reviewsScroller"
            style="height: 400px; overflow-y: auto;">
          <template v-slot:item="{ item }">
            <div class="reviewItem">
              <h3>{{ item.title }}</h3>
              <p><strong>Author:</strong> {{ item.author }}</p>
              <p>{{ item.body }}</p>
            </div>
            <div class="divider"></div>
          </template>
        </VirtualScroller>
      </div>

      <Button @click="showReviewDialog = true" class="addReviewBtn">Add Review</Button>
    </div>
  </div>

  <Dialog v-model:visible="showReviewDialog" header="Add Review" :closable="false" style="width: 50vw;">
    <div>
      <form @submit.prevent="submitReview">
        <div class="form-group">
          <label for="reviewTitle">Title</label>
          <input v-model="newReviewDto.title" type="text" id="reviewTitle" class="p-inputtext p-component" required/>
        </div>
        <div class="form-group">
          <label for="reviewBody">Body</label>
          <textarea v-model="newReviewDto.body" id="reviewBody" class="p-inputtext p-component" rows="4"
                    required></textarea>
        </div>
        <div class="form-group">
          <button type="submit" class="p-button p-component p-button-success" :disabled="isSubmitDisabled">Submit Review</button>
          <button type="button" @click="showReviewDialog = false" class="p-button p-component p-button-secondary">
            Cancel
          </button>
        </div>
      </form>
    </div>
  </Dialog>
</template>


<script setup>

import VirtualScroller from 'primevue/virtualscroller';
import Button from "primevue/button";
import Dialog from "primevue/dialog";
import {ref, onMounted, computed} from "vue";
import {useRoute} from "vue-router";
import {getCurrentUserRoles} from "@/service/AuthenticationService";
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import ClientMenuBar from "@/components/ClientMenuBar.vue";
import axios from "axios";

const route = useRoute();
const ownerUsername = route.params.ownerUsername;
const CurrentUserRole = ref('');
const client = ref(null);
const reviews = ref([]);
const showReviewDialog = ref(false);
let newReviewDto = ref({
  title: '',
  body: '',
  recipient: ''
});

const isSubmitDisabled = computed(() => {
  return !newReviewDto.value.title || !newReviewDto.value.body;
});

onMounted(async () => {
  try {
    const roles = await getCurrentUserRoles();
    if (roles.includes('ROLE_CLIENT')) {
      CurrentUserRole.value = 'ROLE_CLIENT';
    } else if (roles.includes('ROLE_ADMIN')) {
      CurrentUserRole.value = 'ROLE_ADMIN';
    }
    if (ownerUsername) {
      await fetchClientDetails(ownerUsername);
      await fetchClientReviews(ownerUsername)
    }
  } catch (error) {
    console.error('Error fetching roles:', error);
  }
});

const fetchClientDetails = async (ownerUsername) => {
  try {
    const response = await axios.get(`/api/${ownerUsername}`);
    client.value = response.data;
  } catch (error) {
    console.error('Error fetching client details:', error);
  }
};

const fetchClientReviews = async (ownerUsername) => {
  try {
    const response = await axios.get(`/api/reviews/${ownerUsername}`);
    reviews.value = response.data;
  } catch (error) {
    console.error('Error fetching reviews:', error);
  }
};

const submitReview = async () => {
  try {
    newReviewDto.value.recipient = client.value.username;
    await axios.post('/api/addReview', newReviewDto.value);
    showReviewDialog.value = false;
    await fetchClientReviews(ownerUsername);
  } catch (error) {
    console.error('Error submitting review:', error);
  }
};
</script>

<style scoped>
.mainContainer {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.contentWrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  width: 100%;
  max-width: 80em;
  gap: 20px;
}

.clientDetailsBox {
  background-color: #172623;
  color: white;
  border-radius: 8px;
  padding: 20px;
  width: 50%;
  justify-content: center;
}

.clientDetailsBox h1 {
  font-size: 24px;
  color: white;
  margin-bottom: 15px;
}

.clientDetailsBox p {
  font-size: 16px;
  line-height: 1.6;
  color: white;
  margin-bottom: 10px;
}

.clientDetailsBox strong {
  font-weight: bold;
  color: white;
}

.reviewsBox {
  padding: 20px;
  background-color: #f4f4f4;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 50%; /* Adjust width as needed */
}

.reviewsBox h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.reviewsScroller {
  padding: 10px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.reviewItem {
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}

.reviewItem h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.reviewItem p {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.reviewItem strong {
  font-weight: bold;
}

.divider {
  border-top: 1px solid #e0e0e0;
  margin-top: 10px;
  margin-bottom: 10px;
}

.addReviewBtn {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  margin-top: 20px;
  transition: background-color 0.3s;
}

form .form-group {
  margin-bottom: 15px;
}

form .form-group label {
  font-size: 16px;
  color: white;
}

form .form-group input,
form .form-group textarea {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

form .form-group button {
  margin-right: 10px;
}

.p-button {
  width: 20em;
}
</style>
