<template>
  <section class="page-shell quiz-page">
    <h1 class="section-title">八股训练</h1>
    <p class="helper-text">按主题发起训练，提交作答后获得评分、改进建议与下一题。</p>

    <quiz-start-form :loading="starting" @start="handleStart" />

    <quiz-answer-panel
      :session="session"
      :evaluation="evaluation"
      :submitting="submitting"
      :error="errorText"
      @submit="handleSubmitAnswer"
      @apply-next-question="applyNextQuestion"
    />
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import {
  startSession,
  submitAnswer,
  type AnswerResponse,
  type QuizDifficulty,
  type StartSessionResponse,
} from '@/features/quiz/api';
import QuizAnswerPanel from '@/features/quiz/components/QuizAnswerPanel.vue';
import QuizStartForm from '@/features/quiz/components/QuizStartForm.vue';
import { ApiBusinessError } from '@/shared/types/api';

const session = ref<StartSessionResponse | null>(null);
const evaluation = ref<AnswerResponse | null>(null);
const starting = ref(false);
const submitting = ref(false);
const errorText = ref('');

async function handleStart(payload: { topic: string; difficulty: QuizDifficulty }): Promise<void> {
  starting.value = true;
  errorText.value = '';
  evaluation.value = null;
  try {
    session.value = await startSession(payload);
    ElMessage.success('训练会话已创建');
  } catch (error) {
    const text = formatError(error);
    errorText.value = text;
    ElMessage.error(text);
  } finally {
    starting.value = false;
  }
}

async function handleSubmitAnswer(answer: string): Promise<void> {
  if (!session.value) {
    ElMessage.warning('请先创建会话');
    return;
  }

  submitting.value = true;
  errorText.value = '';
  try {
    evaluation.value = await submitAnswer(session.value.sessionId, answer);
    ElMessage.success('评分完成');
  } catch (error) {
    const text = formatError(error);
    errorText.value = text;
    ElMessage.error(text);
  } finally {
    submitting.value = false;
  }
}

function applyNextQuestion(nextQuestion: string): void {
  if (!session.value) {
    return;
  }
  session.value = {
    ...session.value,
    question: nextQuestion,
  };
  evaluation.value = null;
  ElMessage.success('已切换到下一题');
}

function formatError(error: unknown): string {
  if (error instanceof ApiBusinessError) {
    return `${error.message}${error.traceId ? ` (traceId: ${error.traceId})` : ''}`;
  }
  return '训练请求失败，请稍后重试';
}
</script>

<style scoped>
.quiz-page {
  display: grid;
  gap: 16px;
}
</style>
