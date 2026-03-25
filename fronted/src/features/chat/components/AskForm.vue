<template>
  <el-card shadow="never">
    <template #header>
      <div class="form-header">
        <span>Ask From Knowledge Base</span>
        <span class="helper-text">默认 TopK=4，可按召回质量调整</span>
      </div>
    </template>

    <el-form label-position="top" @submit.prevent="handleSubmit">
      <el-form-item label="Question" required>
        <el-input
          v-model="question"
          type="textarea"
          :rows="4"
          placeholder="例如：JVM 内存结构是什么？"
        />
      </el-form-item>

      <el-form-item label="TopK">
        <el-input-number v-model="topK" :min="1" :max="10" />
      </el-form-item>

      <el-button native-type="submit" type="primary" :loading="loading">Ask</el-button>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';

defineProps<{
  loading: boolean;
}>();

const emit = defineEmits<{
  submit: [payload: { question: string; topK: number }];
}>();

const question = ref('');
const topK = ref(4);

function handleSubmit(): void {
  const text = question.value.trim();
  if (!text) {
    return;
  }
  emit('submit', {
    question: text,
    topK: topK.value,
  });
}
</script>

<style scoped>
.form-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>

