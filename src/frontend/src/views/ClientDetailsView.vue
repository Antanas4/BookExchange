<template>
  <ClientMenuBar v-if="CurrentUserRole === 'ROLE_CLIENT'" />
  <AdminMenuBar v-if="CurrentUserRole === 'ROLE_ADMIN'" />
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
          <p><strong>Recipient:</strong> {{ item.recipient }}</p>
          <p>{{ item.body }}</p>
        </div>
        <div class="divider"></div>
      </template>
    </VirtualScroller>
  </div>
</template>

<script setup>

import VirtualScroller from 'primevue/virtualscroller';
import {ref, onMounted} from "vue";
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
    console.error(response.data);
  } catch (error) {
    console.error('Error fetching reviews:', error);
  }
};
</script>

<style scoped>
.clientDetailsBox {
  background-color: #172623;
  color: white;
  border-radius: 8px;
  padding: 20px;
  margin: 20px auto;
  width: 80%;
  max-width: 30em;
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
  margin-top: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
</style>
