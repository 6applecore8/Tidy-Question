<template>
  <el-card class="login-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <h2>Welcome Back</h2>
        <p>登录后可使用文档问答与八股训练</p>
      </div>
    </template>

    <el-form :model="form" label-position="top" @submit.prevent="handleSubmit">
      <el-form-item label="Username" required>
        <el-input v-model="form.username" placeholder="admin" />
      </el-form-item>
      <el-form-item label="Password" required>
        <el-input v-model="form.password" type="password" show-password placeholder="123456" />
      </el-form-item>
      <el-button type="primary" class="submit-btn" :loading="loading" native-type="submit">
        Login
      </el-button>
    </el-form>
  </el-card>
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
  width: min(420px, 92vw);
  border-radius: 16px;
}

.card-header h2 {
  margin: 0;
}

.card-header p {
  margin: 6px 0 0;
  color: #64748b;
}

.submit-btn {
  width: 100%;
}
</style>

