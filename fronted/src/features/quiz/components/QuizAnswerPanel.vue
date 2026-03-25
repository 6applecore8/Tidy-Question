<template>
  <el-card shadow="never">
    <template #header>
      <span>Answer & Evaluation</span>
    </template>

    <el-empty v-if="!session" description="先创建一轮训练会话" />

    <template v-else>
      <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="state-block" />

      <el-descriptions :column="1" border>
        <el-descriptions-item label="Session">{{ session.sessionId }}</el-descriptions-item>
        <el-descriptions-item label="Topic">{{ session.topic }}</el-descriptions-item>
        <el-descriptions-item label="Difficulty">{{ session.difficulty }}</el-descriptions-item>
        <el-descriptions-item label="Question">{{ session.question }}</el-descriptions-item>
      </el-descriptions>

      <el-form class="answer-form" label-position="top" @submit.prevent="handleSubmit">
        <el-form-item label="Your Answer" required>
          <el-input
            v-model="answerText"
            type="textarea"
            :rows="7"
            placeholder="请输入你的作答内容..."
          />
        </el-form-item>
        <el-button native-type="submit" type="primary" :loading="submitting">Submit Answer</el-button>
      </el-form>

      <template v-if="evaluation">
        <el-divider />
        <el-row :gutter="12">
          <el-col :xs="24" :sm="8"><el-statistic title="LLM" :value="evaluation.llmScore" /></el-col>
          <el-col :xs="24" :sm="8"><el-statistic title="Rule" :value="evaluation.ruleScore" /></el-col>
          <el-col :xs="24" :sm="8"><el-statistic title="Final" :value="evaluation.finalScore" /></el-col>
        </el-row>

        <h4>Key Points</h4>
        <div class="tag-wrap">
          <el-tag v-for="item in evaluation.keyPoints" :key="item" class="tag-item">{{ item }}</el-tag>
        </div>

        <h4>Strengths</h4>
        <p>{{ evaluation.strengths }}</p>

        <h4>Improvements</h4>
        <p>{{ evaluation.improvements }}</p>

        <h4>Ideal Answer</h4>
        <p class="ideal-answer">{{ evaluation.idealAnswer }}</p>

        <h4>Next Question</h4>
        <p>{{ evaluation.nextQuestion }}</p>
        <el-button type="success" plain @click="emit('apply-next-question', evaluation.nextQuestion)">
          Use Next Question
        </el-button>
      </template>
    </template>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

import type { AnswerResponse, StartSessionResponse } from '@/features/quiz/api';

const props = defineProps<{
  session: StartSessionResponse | null;
  evaluation: AnswerResponse | null;
  submitting: boolean;
  error: string;
}>();

const emit = defineEmits<{
  submit: [answer: string];
  'apply-next-question': [question: string];
}>();

const answerText = ref('');

watch(
  () => props.session?.question,
  () => {
    answerText.value = '';
  },
);

function handleSubmit(): void {
  const text = answerText.value.trim();
  if (!text) {
    return;
  }
  emit('submit', text);
}
</script>

<style scoped>
.answer-form {
  margin-top: 16px;
}

.state-block {
  margin-bottom: 14px;
}

.tag-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.tag-item {
  margin: 0;
}

.ideal-answer {
  white-space: pre-wrap;
}
</style>
