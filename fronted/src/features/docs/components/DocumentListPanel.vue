<template>
  <el-card shadow="never">
    <template #header>
      <div class="header-row">
        <span>Documents</span>
        <el-button size="small" :loading="loading" @click="emit('refresh')">Refresh</el-button>
      </div>
    </template>

    <el-alert v-if="error" type="error" :closable="false" :title="error" show-icon class="state-block" />

    <el-skeleton v-else-if="loading" :rows="4" animated />

    <el-empty v-else-if="documents.length === 0" description="暂无文档，请先上传" />

    <el-table v-else :data="documents" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="fileName" label="File Name" min-width="180" />
      <el-table-column label="Status" width="140">
        <template #default="{ row }">
          <el-tag :type="tagType(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="chunkCount" label="Chunks" width="100" />
      <el-table-column label="Updated" min-width="170">
        <template #default="{ row }">
          {{ formatDate(row.updatedAt) }}
        </template>
      </el-table-column>
      <el-table-column label="Action" width="140" fixed="right">
        <template #default="{ row }">
          <el-button
            size="small"
            type="primary"
            :loading="indexingIds.has(row.id)"
            @click="emit('index', row.id)"
          >
            Index
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import type { TagProps } from 'element-plus';

import type { DocumentView } from '@/features/docs/api';

defineProps<{
  documents: DocumentView[];
  loading: boolean;
  error: string;
  indexingIds: Set<number>;
}>();

const emit = defineEmits<{
  refresh: [];
  index: [docId: number];
}>();

function tagType(status: string): TagProps['type'] {
  const normalized = status.toUpperCase();
  if (normalized.includes('INDEX')) {
    return 'success';
  }
  if (normalized.includes('FAIL')) {
    return 'danger';
  }
  return 'info';
}

function formatDate(value: string): string {
  if (!value) {
    return '-';
  }
  return new Date(value).toLocaleString();
}
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.state-block {
  margin-bottom: 12px;
}
</style>
