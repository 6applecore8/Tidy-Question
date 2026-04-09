import type { Pinia } from 'pinia';
import { createRouter, createWebHistory } from 'vue-router';

import MainLayout from '@/app/layouts/MainLayout.vue';
import { useAuthStore } from '@/app/store/auth';
import HomePage from '@/pages/home/HomePage.vue';
import SignInPage from '@/pages/signin/SignInPage.vue';
import DocsPage from '@/pages/docs/DocsPage.vue';
import ChatPage from '@/pages/chat/ChatPage.vue';
import QuizPage from '@/pages/quiz/QuizPage.vue';
import ReviewPage from '@/pages/review/ReviewPage.vue';

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
      meta: { requiresAuth: false },
    },
    {
      path: '/product',
      name: 'product',
      component: HomePage,
      meta: { requiresAuth: false },
    },
    {
      path: '/signin',
      name: 'signin',
      component: SignInPage,
      meta: { requiresAuth: false },
    },
    {
      path: '/login',
      redirect: '/signin',
    },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        { path: 'docs', name: 'docs', component: DocsPage, meta: { requiresAuth: true } },
        { path: 'chat', name: 'chat', component: ChatPage, meta: { requiresAuth: true } },
        { path: 'quiz', name: 'quiz', component: QuizPage, meta: { requiresAuth: true } },
        { path: 'review', name: 'review', component: ReviewPage, meta: { requiresAuth: true } },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
});

export function setupRouterGuards(pinia: Pinia): void {
  const authStore = useAuthStore(pinia);

  router.beforeEach((to) => {
    authStore.hydrate();

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
      return { path: '/signin', query: { redirect: to.fullPath } };
    }

    if ((to.path === '/signin' || to.path === '/login') && authStore.isAuthenticated) {
      return '/docs';
    }

    return true;
  });
}
