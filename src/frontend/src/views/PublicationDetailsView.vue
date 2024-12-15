<template>
  <ClientMenuBar v-if="CurrentUserRole === 'ROLE_CLIENT'" />
  <AdminMenuBar v-if="CurrentUserRole === 'ROLE_ADMIN'" />

  <div>
    <div class="publication-details-box" v-if="publication">
      <h1>{{ publication.name }}</h1>
      <p><strong>Author:</strong> {{ publication.author }}</p>
      <p><strong>Title:</strong> {{ publication.title }}</p>
      <p><strong>Price:</strong> {{ publication.price }}</p>
    </div>

    <div class="comments-box">
      <h2>Comments</h2>
      <VirtualScroller
          :items="comments"
          :itemSize="120"
          class="comments-scroller"
          style="height: 400px; overflow-y: auto;">
        <template v-slot:item="{ item }">
          <div :class="['comment-item', { 'comment-reply': item.parentCommentId }]">
            <h3>{{ item.title }}</h3>
            <p>{{ item.body }}</p>
            <Button
                class="replyButton"
                @click="prepareComment(item.id, item.title)">
              Reply
            </Button>
          </div>
          <div class="divider"></div>
        </template>
      </VirtualScroller>
    </div>

    <Button
        @click="prepareComment(null)"
        :disabled="CurrentUserRole === 'ROLE_ADMIN'"
        class="addCommentBtn">
      Add Comment
    </Button>
  </div>

  <Dialog
      v-model:visible="showCommentDialog"
      header="Add Comment"
      :closable="false"
      style="width: 50vw;">
    <form @submit.prevent="submitComment">
      <div v-if="replyingToCommentTitle" class="replying-to">
        <p>Replying to: <strong>{{ replyingToCommentTitle }}</strong></p>
      </div>
      <div class="form-group">
        <label for="comment-title">title</label>
        <textarea v-model="newCommentDto.title" id="comment-title" class="p-input p-component" rows="4" required></textarea>
        <label for="comment-body">Comment</label>
        <textarea v-model="newCommentDto.body" id="comment-body" class="p-input p-component" rows="4" required></textarea>
      </div>
      <div class="form-group">
        <button type="submit" class="p-button p-component p-button-success" :disabled="isSubmitDisabled">Submit</button>
        <button type="button" @click="showCommentDialog = false" class="p-button p-component p-button-secondary">
          Cancel
        </button>
      </div>
    </form>
  </Dialog>
</template>

<script setup>
import VirtualScroller from 'primevue/virtualscroller';
import Button from "primevue/button";
import Dialog from "primevue/dialog";
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import ClientMenuBar from "@/components/ClientMenuBar.vue";
import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import { getCurrentUserRoles } from "@/service/AuthenticationService";
import axios from "axios";

const CurrentUserRole = ref('');
const route = useRoute();
const publicationId = route.params.id;
const publication = ref(null);
const comments = ref([]);
const showCommentDialog = ref(false);
const replyingToCommentTitle = ref(null);
let newCommentDto = ref({
  title:'',
  body: '',
  parentCommentId:'',
  author:'',
  publicationId:''
});

const isSubmitDisabled = computed(() => {
  return !newCommentDto.value.body || !newCommentDto.value.title;
});

onMounted(async () => {
  try {
    const roles = await getCurrentUserRoles();
    if (roles.includes('ROLE_CLIENT')) {
      CurrentUserRole.value = 'ROLE_CLIENT';
    } else if (roles.includes('ROLE_ADMIN')) {
      CurrentUserRole.value = 'ROLE_ADMIN';
    }
    if (publicationId) {
      await fetchPublicationDetails(publicationId);
      await fetchComments(publicationId);
    }
  } catch (error) {
    console.error('Error fetching roles:', error);
  }
});

const fetchPublicationDetails = async (publicationId) => {
  try {
    const response = await axios.get(`/api/publications/${publicationId}`);
    publication.value = response.data;
  } catch (error) {
    console.error("Error fetching publication details", error);
  }
};

const prepareComment = (commentId, commentTitle = null) => {
  newCommentDto.value.parentCommentId = commentId;
  replyingToCommentTitle.value = commentTitle;
  showCommentDialog.value = true;
};


const fetchComments = async (publicationId) => {
  try {
    const response = await axios.get(`/api/comment/getPublicationComments/${publicationId}`);
    comments.value = response.data;
  } catch (error) {
    console.error("Error fetching comments", error);
  }
};

const submitComment = async () => {
  try {
    let authorUsername = await axios.get("/api/currentUser");
    newCommentDto.value.author = authorUsername.data;
    newCommentDto.value.publicationId = publicationId;
    await axios.post('/api/comment/addComment', newCommentDto.value);
    showCommentDialog.value = false;
    console.log(newCommentDto.value);
    await fetchComments(publicationId);
  } catch (error) {
    console.error('Error submitting comment:', error);
  }
};
</script>

<style scoped>
.comments-box {
  padding: 20px;
  background-color: #f4f4f4;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.comments-box h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.comments-scroller {
  padding: 10px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.comment-item {
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}

.comment-item h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.comment-item p {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.divider {
  border-top: 1px solid #e0e0e0;
  margin-top: 10px;
  margin-bottom: 10px;
}

.addCommentBtn {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  margin-top: 20px;
  transition: background-color 0.3s;
}
.comment-reply {
  margin-left: 20px;
  border-left: 2px solid #ddd;
  background-color: #f9f9f9;
  padding-left: 10px;
}

.publication-details-box {
  padding: 20px;
  background-color: #172623;
  border-radius: 10px;
  margin-bottom: 20px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.publication-details-box h1 {
  font-size: 32px;
  margin-bottom: 10px;
}

.publication-details-box p {
  font-size: 16px;
  margin: 5px 0;
}

.replying-to {
  padding: 10px;
  border-left: 4px solid #007bff;
  margin-bottom: 15px;
  border-radius: 5px;
}

.p-input {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #cccccc;
  margin-bottom: 15px;
}

.p-button {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  margin-right: 10px;
}

.p-button-success {
  background-color: #28a745;
  color: #ffffff;
  border: none;
  transition: background-color 0.3s ease;
}

.p-button-success:hover {
  background-color: #218838;
}

.p-button-secondary {
  background-color: #6c757d;
  color: #ffffff;
  border: none;
  transition: background-color 0.3s ease;
}

.p-button-secondary:hover {
  background-color: #5a6268;
}

.dialog-form label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.dialog-form textarea {
  width: 100%;
  border-radius: 5px;
  padding: 10px;
  border: 1px solid #dddddd;
  margin-bottom: 20px;
}

.dialog-form button {
  display: inline-block;
  margin-right: 10px;
}


</style>
