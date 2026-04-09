<template>
  <main class="signin-page">
    <video class="signin-video" autoplay loop muted playsinline>
      <source
        src="https://d8j0ntlcm91z4.cloudfront.net/user_38xzZboKViGWJOttwIXH07lWA1P/hf_20260314_131748_f2ca2a28-fed7-44c8-b9a9-bd9acdd5ec31.mp4"
        type="video/mp4"
      />
    </video>
    <div class="signin-shade"></div>

    <section class="signin-shell">
      <header class="signin-topbar">
        <RouterLink class="signin-brand" to="/">Studio</RouterLink>
        <RouterLink class="signin-back" to="/">返回首页 →</RouterLink>
      </header>

      <div class="signin-content">
        <div class="signin-copy">
          <h1>欢迎回来</h1>
          <p>登录您的账户，继续创造精彩</p>
        </div>

        <LoginCard :loading="loading" @submit="handleLogin" />
      </div>
    </section>
  </main>
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
    ElMessage.success('Logged in');
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/docs';
    router.push(redirect);
  } catch (error) {
    const message =
      error instanceof ApiBusinessError
        ? `${error.message}${error.traceId ? ` (traceId: ${error.traceId})` : ''}`
        : 'Login failed';
    ElMessage.error(message);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=Inter:wght@400;500&display=swap');

.signin-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  color: #ffffff;
  background: #010101;
  font-family: 'Inter', sans-serif;
}

.signin-video {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.08;
  filter: grayscale(0.5) saturate(0.4) brightness(0.35);
}

.signin-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.78), rgba(0, 0, 0, 0.94));
}

.signin-shell {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 16px 16px 40px;
}

.signin-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.signin-brand {
  color: #ffffff;
  font-family: 'Instrument Serif', serif;
  font-size: clamp(2rem, 2.3vw, 2.6rem);
  font-style: italic;
  text-decoration: none;
}

.signin-back {
  color: rgba(255, 255, 255, 0.72);
  font-size: 0.95rem;
  text-decoration: none;
}

.signin-content {
  flex: 1;
  display: grid;
  align-content: center;
  justify-items: center;
  gap: 28px;
  padding: 28px 16px 16px;
}

.signin-copy {
  text-align: center;
}

.signin-copy h1 {
  margin: 0;
  font-family: 'Instrument Serif', serif;
  font-size: clamp(3rem, 5vw, 4.8rem);
  font-weight: 400;
  line-height: 1;
}

.signin-copy p {
  margin: 18px 0 0;
  color: rgba(255, 255, 255, 0.54);
  font-size: 1rem;
}

@media (max-width: 720px) {
  .signin-shell {
    padding-bottom: 28px;
  }

  .signin-back {
    font-size: 0.84rem;
  }

  .signin-content {
    gap: 24px;
  }

  .signin-copy p {
    font-size: 0.92rem;
  }
}
</style>
