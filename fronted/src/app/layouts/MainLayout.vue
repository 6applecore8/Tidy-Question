<template>
  <el-container class="layout">
    <el-header class="layout-header">
      <div class="brand">Tidy Question</div>
      <el-menu mode="horizontal" :default-active="activePath" @select="handleNavigate" class="nav-menu">
        <el-menu-item index="/docs">Docs</el-menu-item>
        <el-menu-item index="/chat">Chat</el-menu-item>
        <el-menu-item index="/quiz">Quiz</el-menu-item>
        <el-menu-item index="/review">Review</el-menu-item>
      </el-menu>
      <div class="header-right">
        <span class="user-name">{{ authStore.username }}</span>
        <el-button size="small" @click="handleLogout">Logout</el-button>
      </div>
    </el-header>

    <el-main class="layout-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';

import { useAuthStore } from '@/app/store/auth';

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const activePath = computed(() => {
  if (route.path.startsWith('/chat')) {
    return '/chat';
  }
  if (route.path.startsWith('/quiz')) {
    return '/quiz';
  }
  if (route.path.startsWith('/review')) {
    return '/review';
  }
  return '/docs';
});

function handleNavigate(path: string): void {
  router.push(path);
}

function handleLogout(): void {
  authStore.logout();
  ElMessage.success('已退出登录');
  router.push('/login');
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.layout-header {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 64px;
  padding: 0 20px;
  background: linear-gradient(90deg, #0b766e, #155e75);
}

.brand {
  color: #f8fafc;
  font-size: 20px;
  font-weight: 700;
  min-width: 160px;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
  background: transparent;
}

:deep(.el-menu--horizontal > .el-menu-item) {
  color: #dcfce7;
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  color: #ffffff;
  border-bottom-color: #ffffff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  color: #ecfeff;
  font-size: 14px;
}

.layout-main {
  padding: 0;
}

@media (max-width: 768px) {
  .layout-header {
    height: auto;
    flex-wrap: wrap;
    padding: 12px;
  }

  .brand {
    min-width: 120px;
    font-size: 18px;
  }

  .header-right {
    margin-left: auto;
  }

  .nav-menu {
    width: 100%;
    order: 3;
  }
}
</style>
