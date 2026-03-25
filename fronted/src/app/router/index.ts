import type { Pinia } from 'pinia';
import { createRouter, createWebHistory } from 'vue-router';

import MainLayout from '@/app/layouts/MainLayout.vue';
import { useAuthStore } from '@/app/store/auth';
import LoginPage from '@/pages/login/LoginPage.vue';
import DocsPage from '@/pages/docs/DocsPage.vue';
import ChatPage from '@/pages/chat/ChatPage.vue';
import QuizPage from '@/pages/quiz/QuizPage.vue';
import ReviewPage from '@/pages/review/ReviewPage.vue';

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/docs' },
        { path: 'docs', name: 'docs', component: DocsPage, meta: { requiresAuth: true } },
        { path: 'chat', name: 'chat', component: ChatPage, meta: { requiresAuth: true } },
        { path: 'quiz', name: 'quiz', component: QuizPage, meta: { requiresAuth: true } },
        { path: 'review', name: 'review', component: ReviewPage, meta: { requiresAuth: true } },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/docs' },
  ],
});

export function setupRouterGuards(pinia: Pinia): void {
  const authStore = useAuthStore(pinia);

  router.beforeEach((to) => {
    authStore.hydrate();

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
      return { path: '/login', query: { redirect: to.fullPath } };
    }

    if (to.path === '/login' && authStore.isAuthenticated) {
      return '/docs';
    }

    return true;
  });
}
