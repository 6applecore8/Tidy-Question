<template>
  <div class="login-page">
    <div class="overlay"></div>
    <login-card :loading="loading" @submit="handleLogin" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';

import { useAuthStore } from '@/app/store/auth';
import { login } from '@/features/auth/api';
import LoginCard from '@/features/auth/components/LoginCard.vue';
import { ApiBusinessError } from '@/shared/types/api';

const loading = ref(false);
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

async function handleLogin(payload: { username: string; password: string }): Promise<void> {
  loading.value = true;
  try {
    const result = await login(payload);
    authStore.setSession(result);
    ElMessage.success('登录成功');
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/docs';
    router.push(redirect);
  } catch (error) {
    const message =
      error instanceof ApiBusinessError ? `${error.message}${error.traceId ? ` (traceId: ${error.traceId})` : ''}` : '登录失败';
    ElMessage.error(message);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  position: relative;
  overflow: hidden;
}

.overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(140deg, rgba(15, 118, 110, 0.25), rgba(14, 116, 144, 0.2));
  backdrop-filter: blur(1px);
}

.login-page > *:not(.overlay) {
  position: relative;
  z-index: 1;
}
</style>
