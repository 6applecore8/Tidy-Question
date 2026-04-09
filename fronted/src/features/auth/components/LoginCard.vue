<template>
  <section class="login-card" aria-label="Login form">
    <el-form class="auth-form" :model="form" label-position="top" @submit.prevent="handleSubmit">
      <el-form-item label="邮箱地址" required>
        <el-input v-model="form.username" placeholder="admin" />
      </el-form-item>

      <el-form-item label="密码" required>
        <el-input v-model="form.password" type="password" show-password placeholder="123456" />
      </el-form-item>

      <div class="assist-row">
        <span>使用默认账户登录</span>
        <button class="assist-link" type="button">忘记密码?</button>
      </div>

      <el-button class="primary-submit" :loading="loading" native-type="submit">登录</el-button>

      <div class="divider">或</div>

      <button class="ghost-action" type="button">使用 Google 登录</button>
      <button class="ghost-action" type="button">使用 Apple 登录</button>

      <p class="foot-note">还没有账户? <strong>注册</strong></p>
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
  width: min(480px, 100%);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.auth-form :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.72);
  font-size: 0.9rem;
  font-weight: 500;
}

.auth-form :deep(.el-input__wrapper) {
  min-height: 58px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  background: rgba(8, 8, 9, 0.9);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.06),
    0 0 0 1px rgba(255, 255, 255, 0.02);
}

.auth-form :deep(.el-input__inner) {
  color: #ffffff;
  font-size: 1rem;
}

.auth-form :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.28);
}

.auth-form :deep(.el-input__suffix-inner) {
  color: rgba(255, 255, 255, 0.42);
}

.assist-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: -4px;
  margin-bottom: 16px;
  color: rgba(255, 255, 255, 0.42);
  font-size: 0.9rem;
}

.assist-link {
  border: none;
  padding: 0;
  color: rgba(112, 162, 255, 0.82);
  font: inherit;
  background: transparent;
  cursor: pointer;
}

.primary-submit {
  width: 100%;
  min-height: 56px;
  border: none;
  border-radius: 18px;
  color: #5f93e0;
  font-size: 1.1rem;
  font-weight: 700;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(245, 245, 245, 0.94));
  box-shadow: 0 14px 38px rgba(255, 255, 255, 0.08);
}

.primary-submit:hover,
.primary-submit:focus {
  color: #4e84d8;
  background: linear-gradient(180deg, rgba(255, 255, 255, 1), rgba(245, 245, 245, 0.96));
}

.divider {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 16px;
  margin: 18px 0;
  color: rgba(255, 255, 255, 0.34);
  font-size: 0.9rem;
}

.divider::before,
.divider::after {
  content: '';
  height: 1px;
  background: rgba(255, 255, 255, 0.12);
}

.ghost-action {
  min-height: 56px;
  margin-bottom: 14px;
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: 18px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 1rem;
  background: rgba(0, 0, 0, 0.36);
  cursor: pointer;
}

.ghost-action:last-of-type {
  margin-bottom: 0;
}

.foot-note {
  margin: 28px 0 0;
  color: rgba(255, 255, 255, 0.38);
  font-size: 0.95rem;
  text-align: center;
}

.foot-note strong {
  color: rgba(255, 255, 255, 0.92);
  font-weight: 600;
}

@media (max-width: 720px) {
  .login-card {
    width: min(420px, 100%);
  }

  .auth-form :deep(.el-input__wrapper) {
    min-height: 54px;
  }

  .ghost-action,
  .primary-submit {
    min-height: 54px;
  }
}
</style>
