<template>
  <ClientMenuBar v-if="CurrentUserRole === 'ROLE_CLIENT'"/>
  <AdminMenuBar v-if="CurrentUserRole === 'ROLE_ADMIN'"/>
  <div class="card">
    <DataView :value="publications" paginator :rows="5">
      <template #list="slotProps">
        <div class="publication-list">
          <div v-for="(item, index) in slotProps.items" :key="index" class="publication-item">
            <div :class="['publication-container', index !== 0 ? 'border-top' : '']">
              <router-link :to="'/client/' + item.ownerUsername">
                <Tag :value="item.ownerUsername" />
              </router-link>
              <div class="publication-details">
                <div class="title-author">
                  <span class="title">{{ item.title }}</span>
                  <div class="author">{{ item.author }}</div>
                  <div class="author">{{ item.status }}</div>
                </div>
              </div>
              <div class="buy-button-container">
                <span class="price-xl">${{ item.price }}</span>
                <div class="button-group">
                  <Button class="buy-button" @click=handleBuyPublication(item)>Buy</Button>
                  <Button class="reserve-button">Reserve</Button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </DataView>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import Button from 'primevue/button';
import DataView from 'primevue/dataview';
import Tag from "primevue/tag";
import {getPublicationsShopService, buyPublication} from '@/service/ManagePublicationsService';
import ClientMenuBar from "@/components/ClientMenuBar.vue";
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import {getCurrentUserRoles} from "@/service/AuthenticationService";

const publications = ref([]);
const CurrentUserRole = ref('');

onMounted(async () => {
  try {
    publications.value = await getPublicationsShopService();
    console.log(publications.value);

    const roles = await getCurrentUserRoles();
    if (roles.includes('ROLE_CLIENT')) {
      CurrentUserRole.value = 'ROLE_CLIENT';
    } else if (roles.includes('ROLE_ADMIN')) {
      CurrentUserRole.value = 'ROLE_ADMIN';
    }
  } catch (error) {
    console.error('Error fetching data:', error);
  }
});

const handleBuyPublication = async (publication) => {
  try {
    await buyPublication(publication);
    publications.value = await getPublicationsShopService();
  } catch (error) {
    console.error('Error fetching publications:', error);
  }
}
</script>

<style scoped>
.publication-list {
  display: flex;
  flex-direction: column;
}

.publication-item {
  padding: 15px;
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #ddd;
}

.publication-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  width: 100%;
  align-items: center;
}

.border-top {
  border-top: 1px solid #ddd;
}

.owner-tag {
  flex: 0 0 auto;
}

.publication-details {
  flex: 1 1 auto;
  padding-left: 15px;
}

.title-author .title {
  font-weight: bold;
  font-size: 1.1em;
}

.title-author .author {
  color: #777;
  font-size: 0.9em;
}

.buy-button-container {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.buy-button, .reserve-button {
  width: 100px;
  margin: 5px;
}

.price-xl {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 10px;
}
</style>
