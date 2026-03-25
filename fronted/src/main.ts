import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

import App from '@/app/App.vue';
import { router, setupRouterGuards } from '@/app/router';
import '@/app/styles/global.css';

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
setupRouterGuards(pinia);
app.use(router);
app.use(ElementPlus);

app.mount('#app');
