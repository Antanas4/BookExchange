import {createApp} from 'vue';
import PrimeVue from 'primevue/config';
import Aura from '@primevue/themes/aura';
import 'primeicons/primeicons.css';
import App from './App.vue';

import {createRouter, createWebHistory} from 'vue-router'
import ManageUsersView from "@/views/ManageUsersView.vue";
import ManagePublicationsView from "@/views/ManagePublicationsView.vue";
import LoginView from "@/views/LoginView.vue";
import PublicationsShopView from "@/views/PublicationsShopView.vue";
import PermissionDeniedView from "@/views/PermissionDeniedView.vue";
import {getCurrentUserRoles, isLoggedIn} from "@/service/AuthenticationService";
import ClientDetailsView from "@/views/ClientDetailsView.vue";
import MyProfileView from "@/views/MyProfileView.vue";
import PublicationDetailsView from "@/views/PublicationDetailsView.vue";

"@/service/AuthenticationService"

// const getCurrentUserRoles = async () => {
//     try {
//         const response = await axios.get('/api/currentUser/roles');
//         return response.data || [];
//     } catch (error) {
//         console.error("Error fetching user roles:", error);
//         return [];
//     }
// };
//
// const isLoggedIn = async () => {
//     try {
//         const response = await axios.get('/api/currentUser');
//         return response.status === 200;
//     } catch (error) {
//         return false;
//     }
// };

const routes = [
    {
        path: '/users',
        component: ManageUsersView,
        meta: { requiresRole: 'ROLE_ADMIN' }
    },
    {
        path: '/publications',
        component: ManagePublicationsView,
        meta: { requiresRole: 'ROLE_ADMIN' }
    },
    { path: '/client/:ownerUsername', component: ClientDetailsView, props: true },
    { path: '/publications/about/:id', component: PublicationDetailsView, props: true },
    { path: '/myProfile/:clientUsername', component: MyProfileView, props: true },
    { path: '/auth', component: LoginView },
    { path: '/publications/shop', component: PublicationsShopView },
    { path: '/403', component: PermissionDeniedView },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach(async (to, from, next) => {
    const authenticated = await isLoggedIn();

    if (to.path !== '/auth' && !authenticated) {
        next('/auth');
    } else if (to.meta.requiresRole) {
        const roles = await getCurrentUserRoles();
        if (roles.includes(to.meta.requiresRole)) {
            next();
        } else {
            next('/403');
        }
    } else {
        next();
    }
});

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
