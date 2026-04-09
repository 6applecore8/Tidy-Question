<template>
  <main class="login-film">
    <video class="background-video" autoPlay loop muted playsinline>
      <source
        src="https://d8j0ntlcm91z4.cloudfront.net/user_38xzZboKViGWJOttwIXH07lWA1P/hf_20260314_131748_f2ca2a28-fed7-44c8-b9a9-bd9acdd5ec31.mp4"
        type="video/mp4"
      />
    </video>

    <div class="cinema-shade"></div>

    <div class="fireflies" aria-hidden="true">
      <span v-for="firefly in fireflies" :key="firefly.id" class="firefly" :style="firefly.style"></span>
    </div>

    <section class="hero-shell">
      <header class="glass-nav liquid-glass">
        <div class="brand-mark">
          <span class="brand-glyph">T</span>
          <span>Tidy Question</span>
        </div>

        <nav class="nav-links" aria-label="Primary navigation">
          <a href="#">Home</a>
          <a href="#">Studio</a>
          <a href="#">About</a>
          <a href="#">Journal</a>
          <a href="#">Reach Us</a>
        </nav>

        <button class="nav-cta liquid-glass" type="button" @click="openSignIn">Sign In</button>
      </header>

      <div class="hero-stage">
        <p class="hero-kicker">Coming soon</p>

        <div class="title-wrap">
          <h1>
            Tidy <em>Studio</em>
          </h1>
          <div class="title-lens liquid-glass" aria-hidden="true"></div>
        </div>

        <p class="hero-copy">
          Upload source material, ask with citations, rehearse with pressure, and return with cleaner thinking.
        </p>

        <button class="hero-cta liquid-glass" type="button" @click="openSignIn">Sign In</button>
      </div>

      <div class="floating-scene" aria-hidden="true">
        <div class="focus-ribbon liquid-glass">Focus</div>

        <div class="editor-card liquid-glass">
          <div class="editor-actions">
            <span>Frame</span>
            <span>Guide</span>
            <span>Refine</span>
            <span>Source</span>
          </div>
          <div class="editor-body">
            <p>Source notes define the answer. Source notes define the answer.</p>
            <p>Question, retrieve, compare, improve. Question, retrieve, compare, improve.</p>
            <p>Weak points move into focus. Weak points move into focus.</p>
            <p>Every document becomes practice. Every document becomes practice.</p>
          </div>
          <div class="editor-lens liquid-glass"></div>
        </div>
      </div>
    </section>

    <transition name="signin-fade">
      <div v-if="signInVisible" class="signin-overlay" @click.self="closeSignIn">
        <div class="signin-modal">
          <button class="close-button" type="button" aria-label="Close sign in" @click="closeSignIn">x</button>
          <login-card :loading="loading" @submit="handleLogin" />
        </div>
      </div>
    </transition>
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
const signInVisible = ref(false);
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const fireflies = [
  { id: 1, style: { left: '8%', top: '14%', width: '6px', height: '6px', animationDelay: '0s' } },
  { id: 2, style: { left: '18%', top: '28%', width: '8px', height: '8px', animationDelay: '1.2s' } },
  { id: 3, style: { left: '62%', top: '18%', width: '7px', height: '7px', animationDelay: '0.8s' } },
  { id: 4, style: { left: '84%', top: '26%', width: '6px', height: '6px', animationDelay: '1.8s' } },
  { id: 5, style: { left: '70%', top: '74%', width: '8px', height: '8px', animationDelay: '0.5s' } },
  { id: 6, style: { left: '22%', top: '78%', width: '7px', height: '7px', animationDelay: '2.2s' } },
] as const;

function openSignIn(): void {
  signInVisible.value = true;
}

function closeSignIn(): void {
  if (!loading.value) {
    signInVisible.value = false;
  }
}

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

.login-film {
  --font-display: 'Instrument Serif', serif;
  --font-body: 'Inter', sans-serif;
  --background: 201 100% 13%;
  --foreground: 0 0% 100%;
  --muted-foreground: 240 4% 66%;
  --primary: 0 0% 100%;
  --primary-foreground: 0 0% 4%;
  --secondary: 0 0% 10%;
  --muted: 0 0% 10%;
  --accent: 0 0% 10%;
  --border: 0 0% 18%;
  --input: 0 0% 18%;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  color: hsl(var(--foreground));
  background: hsl(var(--background));
  font-family: var(--font-body);
}

.background-video {
  position: absolute;
  inset: 0;
  z-index: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cinema-shade {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(180deg, rgba(5, 11, 18, 0.22), rgba(5, 11, 18, 0.58));
}

.fireflies {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
}

.firefly {
  position: absolute;
  display: block;
  border-radius: 999px;
  background: rgba(255, 239, 164, 0.95);
  box-shadow:
    0 0 12px rgba(255, 230, 132, 0.66),
    0 0 30px rgba(255, 210, 90, 0.34);
  animation: drift-glow 5.6s ease-in-out infinite;
}

.hero-shell {
  position: relative;
  z-index: 3;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 0 28px 32px;
}

.glass-nav {
  position: relative;
  z-index: 10;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 28px;
  width: min(100%, 1600px);
  margin: 14px auto 0;
  padding: 14px 22px;
  border-radius: 14px;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: hsl(var(--foreground));
  font-family: var(--font-display);
  font-size: 2rem;
  line-height: 1;
  white-space: nowrap;
}

.brand-glyph {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  color: #0b1219;
  font-family: var(--font-body);
  font-size: 1rem;
  font-weight: 700;
  background: rgba(255, 255, 255, 0.9);
}

.nav-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: clamp(18px, 2.4vw, 40px);
  justify-self: center;
}

.nav-links a {
  border: none;
  padding: 0;
  color: hsl(var(--muted-foreground));
  font: inherit;
  font-size: 0.88rem;
  font-weight: 500;
  text-decoration: none;
  background: transparent;
  transition: color 160ms ease, opacity 160ms ease;
  cursor: pointer;
}

.nav-links a:first-child {
  color: hsl(var(--foreground));
}

.nav-links a:hover {
  color: hsl(var(--foreground));
}

.nav-cta {
  justify-self: end;
  min-width: 132px;
  min-height: 42px;
  padding: 0 18px;
  border-radius: 999px;
  color: hsl(var(--foreground));
  font-size: 0.82rem;
  font-weight: 500;
  letter-spacing: 0.01em;
  cursor: pointer;
}

.hero-stage {
  position: relative;
  z-index: 4;
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0 220px;
  text-align: center;
}

.hero-kicker {
  margin: 0 0 18px;
  color: hsl(var(--muted-foreground));
  font-size: 0.95rem;
}

.title-wrap {
  position: relative;
  width: 100%;
  max-width: 1180px;
}

.title-wrap h1 {
  margin: 0;
  color: hsl(var(--foreground));
  font-family: var(--font-display);
  font-size: clamp(4.7rem, 13vw, 10rem);
  font-weight: 400;
  line-height: 0.9;
  letter-spacing: -0.04em;
  text-shadow: 0 18px 64px rgba(255, 255, 255, 0.2);
}

.title-wrap em {
  font-style: normal;
  color: hsl(var(--muted-foreground));
}

.title-lens {
  position: absolute;
  left: 22%;
  right: 22%;
  bottom: 8%;
  height: 52px;
  border-radius: 14px;
}

.hero-copy {
  max-width: 760px;
  margin: 30px auto 0;
  color: hsl(var(--muted-foreground));
  font-size: 1.08rem;
  line-height: 1.75;
}

.hero-cta {
  margin-top: 34px;
  min-width: 240px;
  min-height: 58px;
  border-radius: 999px;
  color: hsl(var(--foreground));
  font-size: 1rem;
  transition: transform 160ms ease;
  cursor: pointer;
}

.hero-cta:hover {
  transform: scale(1.03);
}

.floating-scene {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 30px;
  z-index: 3;
  width: min(100%, 1560px);
  height: 262px;
  margin: 0 auto;
  pointer-events: none;
}

.focus-ribbon,
.editor-card {
  position: absolute;
}

.focus-ribbon {
  left: 1.5%;
  bottom: 198px;
  width: min(360px, 24vw);
  height: 38px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.82);
  font-size: 0.76rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.editor-card {
  left: 1.5%;
  bottom: 0;
  width: min(500px, 32vw);
  height: 218px;
  border-radius: 18px;
  padding: 18px;
}

.editor-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.editor-actions span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 78px;
  min-height: 34px;
  border-radius: 999px;
  color: rgba(255, 255, 255, 0.86);
  font-size: 0.8rem;
  background: rgba(0, 0, 0, 0.24);
}

.editor-body {
  position: relative;
  max-width: 84%;
  color: rgba(255, 255, 255, 0.74);
  font-size: 0.96rem;
  line-height: 1.72;
}

.editor-body p {
  margin: 0 0 10px;
}

.editor-lens {
  position: absolute;
  left: 18px;
  right: 18px;
  bottom: 34px;
  height: 54px;
  border-radius: 14px;
}

.liquid-glass {
  background: rgba(255, 255, 255, 0.01);
  background-blend-mode: luminosity;
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  border: none;
  box-shadow: inset 0 1px 1px rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.liquid-glass::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1.4px;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.45) 0%,
    rgba(255, 255, 255, 0.15) 20%,
    rgba(255, 255, 255, 0) 40%,
    rgba(255, 255, 255, 0) 60%,
    rgba(255, 255, 255, 0.15) 80%,
    rgba(255, 255, 255, 0.45) 100%
  );
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.signin-overlay {
  position: fixed;
  inset: 0;
  z-index: 60;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgba(4, 10, 15, 0.52);
  backdrop-filter: blur(14px);
}

.signin-modal {
  position: relative;
  width: min(460px, 100%);
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 3;
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 999px;
  color: rgba(255, 255, 255, 0.92);
  font-size: 1.1rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(6px);
  cursor: pointer;
}

.signin-fade-enter-active,
.signin-fade-leave-active {
  transition: opacity 180ms ease;
}

.signin-fade-enter-from,
.signin-fade-leave-to {
  opacity: 0;
}

@media (max-width: 1080px) {
  .glass-nav {
    grid-template-columns: auto 1fr auto;
    gap: 18px;
  }

  .nav-links {
    gap: 18px;
  }

  .title-lens {
    left: 18%;
    right: 18%;
  }

  .focus-ribbon {
    left: 2%;
    width: min(320px, 28vw);
  }

  .editor-card {
    left: 2%;
    width: min(430px, 38vw);
  }
}

@keyframes drift-glow {
  0%,
  100% {
    opacity: 0.35;
    transform: translate3d(0, 0, 0) scale(0.9);
  }

  50% {
    opacity: 1;
    transform: translate3d(0, -10px, 0) scale(1.1);
  }
}

@media (max-width: 820px) {
  .hero-shell {
    padding: 0 16px 18px;
  }

  .glass-nav {
    grid-template-columns: 1fr;
    justify-items: center;
    gap: 16px;
    padding: 18px 16px;
  }

  .brand-mark {
    font-size: 1.6rem;
  }

  .nav-links {
    flex-wrap: wrap;
    gap: 12px 20px;
  }

  .nav-links a {
    font-size: 0.86rem;
  }

  .nav-cta {
    min-width: 120px;
  }

  .hero-stage {
    padding: 56px 0 236px;
  }

  .title-wrap h1 {
    font-size: clamp(3.4rem, 22vw, 5.8rem);
  }

  .title-lens {
    left: 12%;
    right: 12%;
    height: 44px;
  }

  .hero-copy {
    font-size: 0.98rem;
    line-height: 1.65;
  }

  .floating-scene {
    height: 250px;
    bottom: 16px;
  }

  .focus-ribbon {
    left: 50%;
    bottom: 194px;
    width: min(360px, 88vw);
    transform: translateX(-50%);
  }

  .editor-card {
    left: 50%;
    width: min(420px, 92vw);
    height: 214px;
    padding: 14px;
    transform: translateX(-50%);
  }

  .editor-actions {
    flex-wrap: wrap;
  }

  .editor-actions span {
    min-width: 72px;
  }

  .editor-body {
    max-width: 100%;
    font-size: 0.86rem;
    line-height: 1.58;
  }

  .editor-lens {
    bottom: 24px;
    height: 42px;
  }
}
</style>
