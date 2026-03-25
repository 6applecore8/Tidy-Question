<template>
  <section class="page-shell chat-page">
    <h1 class="section-title">知识问答</h1>
    <p class="helper-text">输入问题后，系统会结合已索引资料返回答案与引用来源。</p>

    <ask-form :loading="loading" @submit="handleAsk" />
    <answer-card :response="answer" :error="errorText" />
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import { askQuestion, type AskResponse } from '@/features/chat/api';
import AnswerCard from '@/features/chat/components/AnswerCard.vue';
import AskForm from '@/features/chat/components/AskForm.vue';
import { ApiBusinessError } from '@/shared/types/api';

const loading = ref(false);
const answer = ref<AskResponse | null>(null);
const errorText = ref('');

async function handleAsk(payload: { question: string; topK: number }): Promise<void> {
  loading.value = true;
  errorText.value = '';
  try {
    answer.value = await askQuestion(payload);
    ElMessage.success('回答已生成');
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
  return '提问失败，请稍后重试';
}
</script>

<style scoped>
.chat-page {
  display: grid;
  gap: 16px;
}
</style>
