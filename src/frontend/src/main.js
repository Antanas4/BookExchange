import {createApp} from 'vue';
import PrimeVue from 'primevue/config';
import Aura from '@primevue/themes/aura';
import 'primeicons/primeicons.css';
import App from './App.vue';

import {createRouter, createWebHistory} from 'vue-router'
import ManageUsersView from "@/views/ManageUsersView.vue";
import ManagePublicationsView from "@/views/ManagePublicationsView.vue";
import LoginView from "@/views/LoginView.vue";

const routes = [
    {path: '/users', component: ManageUsersView},
    {path: '/publications', component: ManagePublicationsView},
    {path: '/auth', component: LoginView}
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

const app = createApp(App);
app.use(PrimeVue, {
    theme: {
        preset: Aura,
        options: {
            prefix: 'p',
            darkModeSelector: 'system',
            cssLayer: false
        }
    }
});

app.use(router);
app.mount('#app');
