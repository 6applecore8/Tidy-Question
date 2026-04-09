<template>
  <section class="login-card" aria-label="Login form">
    <div class="card-header">
      <p>Studio Access</p>
      <h2>Sign in</h2>
      <span>Use the default account below.</span>
    </div>

    <el-form class="glass-form" :model="form" label-position="top" @submit.prevent="handleSubmit">
      <el-form-item label="Username" required>
        <el-input v-model="form.username" placeholder="admin" />
      </el-form-item>
      <el-form-item label="Password" required>
        <el-input v-model="form.password" type="password" show-password placeholder="123456" />
      </el-form-item>
      <el-button class="liquid-submit" :loading="loading" native-type="submit">
        Sign In
      </el-button>
    </el-form>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue';

interface LoginFormModel {
  username: string;
  password: string;
}

defineProps<{
  loading: boolean;
}>();

const emit = defineEmits<{
  submit: [payload: LoginFormModel];
}>();

const form = reactive<LoginFormModel>({
  username: 'admin',
  password: '123456',
});

function handleSubmit(): void {
  if (!form.username.trim() || !form.password) {
    return;
  }
  emit('submit', {
    username: form.username.trim(),
    password: form.password,
  });
}
</script>

<style scoped>
.login-card {
  position: relative;
  overflow: hidden;
  width: min(450px, 100%);
  padding: 28px;
  border: 1px solid rgba(255, 255, 255, 0.42);
  border-radius: 8px;
  color: #f7fbf7;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.22), rgba(255, 255, 255, 0.045)),
    rgba(7, 18, 26, 0.58);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.58),
    inset 0 -80px 120px rgba(157, 224, 240, 0.12),
    0 34px 86px rgba(0, 0, 0, 0.48);
  backdrop-filter: blur(26px) saturate(1.4);
}

.login-card::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: linear-gradient(120deg, rgba(255, 255, 255, 0.34), transparent 26%, transparent 70%, rgba(255, 255, 255, 0.16));
  opacity: 0.72;
}

.card-header,
.glass-form {
  position: relative;
  z-index: 1;
}

.card-header {
  margin-bottom: 26px;
}

.card-header p,
.card-header span {
  margin: 0;
  color: rgba(242, 231, 184, 0.78);
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.card-header h2 {
  margin: 8px 0 8px;
  font-family: 'Instrument Serif', 'Bodoni 72', Didot, Georgia, serif;
  font-size: 40px;
  font-weight: 400;
  line-height: 1;
}

.glass-form :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.82);
  font-weight: 700;
}

.glass-form :deep(.el-input__wrapper) {
  min-height: 48px;
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.12);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.36);
  backdrop-filter: blur(16px);
}

.glass-form :deep(.el-input__inner) {
  color: #ffffff;
  font-weight: 700;
}

.glass-form :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.48);
}

.liquid-submit {
  width: 100%;
  min-height: 54px;
  margin-top: 8px;
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  color: #071016;
  font-size: 18px;
  font-weight: 900;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.18) 33%, transparent 34%),
    linear-gradient(135deg, #fff36b 0%, #f1c91f 52%, #9b6b0a 100%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.96),
    inset 0 -12px 22px rgba(95, 65, 4, 0.28),
    0 18px 44px rgba(241, 207, 67, 0.32);
  backdrop-filter: blur(18px) saturate(1.3);
}

.liquid-submit:hover,
.liquid-submit:focus {
  color: #071016;
  border-color: rgba(255, 255, 255, 0.86);
  transform: translateY(-1px);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(255, 255, 255, 0.22) 33%, transparent 34%),
    linear-gradient(135deg, #fff88c 0%, #f4cf31 52%, #b47b0b 100%);
}

@media (max-width: 680px) {
  .login-card {
    padding: 22px;
  }

  .card-header h2 {
    font-size: 34px;
  }
}
</style>
