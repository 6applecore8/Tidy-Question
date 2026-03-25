<template>
  <el-card shadow="never">
    <template #header>
      <span>Start Quiz Session</span>
    </template>

    <el-form label-position="top" @submit.prevent="handleStart">
      <el-form-item label="Topic" required>
        <el-input v-model="topic" placeholder="例如：Spring" />
      </el-form-item>

      <el-form-item label="Difficulty" required>
        <el-select v-model="difficulty" style="width: 180px">
          <el-option label="Easy" value="easy" />
          <el-option label="Medium" value="medium" />
          <el-option label="Hard" value="hard" />
        </el-select>
      </el-form-item>

      <el-button native-type="submit" type="primary" :loading="loading">Start</el-button>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';

import type { QuizDifficulty } from '@/features/quiz/api';

defineProps<{
  loading: boolean;
}>();

const emit = defineEmits<{
  start: [payload: { topic: string; difficulty: QuizDifficulty }];
}>();

const topic = ref('Spring');
const difficulty = ref<QuizDifficulty>('medium');

function handleStart(): void {
  const text = topic.value.trim();
  if (!text) {
    return;
  }
  emit('start', {
    topic: text,
    difficulty: difficulty.value,
  });
}
</script>
