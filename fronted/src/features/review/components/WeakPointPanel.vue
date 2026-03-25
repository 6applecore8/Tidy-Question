<template>
  <el-card shadow="never">
    <template #header>
      <div class="header-row">
        <span>Weak Points</span>
        <el-button size="small" :loading="loading" @click="emit('refresh')">Refresh</el-button>
      </div>
    </template>

    <el-alert v-if="error" type="error" :title="error" :closable="false" show-icon class="state-block" />

    <el-skeleton v-else-if="loading" :rows="4" animated />

    <el-empty v-else-if="items.length === 0" description="暂无数据，先去完成几轮训练" />

    <el-table v-else :data="items" stripe>
      <el-table-column prop="topic" label="Topic" min-width="180" />
      <el-table-column label="Avg Score" width="130">
        <template #default="{ row }">
          {{ Number(row.avgScore).toFixed(1) }}
        </template>
      </el-table-column>
      <el-table-column prop="attemptCount" label="Attempts" width="120" />
      <el-table-column prop="suggestion" label="Suggestion" min-width="260" show-overflow-tooltip />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import type { WeakPointItem } from '@/features/review/api';

defineProps<{
  loading: boolean;
  error: string;
  items: WeakPointItem[];
}>();

const emit = defineEmits<{
  refresh: [];
}>();
</script>

<style scoped>
.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.state-block {
  margin-bottom: 12px;
}
</style>
