<template>
  <section class="page-shell">
    <h1 class="section-title">复盘薄弱点</h1>
    <p class="helper-text">根据历史训练成绩，展示平均分与建议复习方向。</p>

    <weak-point-panel :loading="loading" :error="errorText" :items="items" @refresh="loadWeakPoints" />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { fetchWeakPoints, type WeakPointItem } from '@/features/review/api';
import WeakPointPanel from '@/features/review/components/WeakPointPanel.vue';
import { ApiBusinessError } from '@/shared/types/api';

const items = ref<WeakPointItem[]>([]);
const loading = ref(false);
const errorText = ref('');

onMounted(() => {
  void loadWeakPoints();
});

async function loadWeakPoints(): Promise<void> {
  loading.value = true;
  errorText.value = '';
  try {
    items.value = await fetchWeakPoints();
  } catch (error) {
    const text = formatError(error);
    errorText.value = text;
    ElMessage.error(text);
  } finally {
    loading.value = false;
  }
}

function formatError(error: unknown): string {
  if (error instanceof ApiBusinessError) {
    return `${error.message}${error.traceId ? ` (traceId: ${error.traceId})` : ''}`;
  }
  return '加载失败，请稍后重试';
}
</script>
